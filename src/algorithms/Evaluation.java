package algorithms;
import java.util.ArrayList;
import java.util.Stack;
import java.awt.Point;

public class Evaluation {
	private static boolean isMember(ArrayList<Point> points, Point p){
		for (Point point:points) if (point.equals(p)) return true; return false;
	}
	public static boolean isValide(ArrayList<Point> origPoints, ArrayList<Point> fvs){
		ArrayList<Point> vertices = new ArrayList<Point>();
		for (Point p:origPoints) if (!isMember(fvs,p)) vertices.add((Point)p.clone());

		//Looking for loops in subgraph induced by origPoint \setminus fvs
		while (!vertices.isEmpty()){
			ArrayList<Point> green = new ArrayList<Point>();
			green.add((Point)vertices.get(0).clone());
			ArrayList<Point> black = new ArrayList<Point>();

			while (!green.isEmpty()){
				for (Point p:neighbor(green.get(0),vertices)){
					if (green.get(0).equals(p)) continue;
					if (isMember(black,p)) return false;
					if (isMember(green,p)) return false;
					green.add((Point)p.clone());
				}
				black.add((Point)green.get(0).clone());
				vertices.remove(green.get(0));
				green.remove(0);
			}
		}

		return true;
	}
	public static ArrayList<Point> neighbor(Point p, ArrayList<Point> vertices){
		ArrayList<Point> result = new ArrayList<Point>();

		for (Point point:vertices) if (point.distance(p)<100 && !point.equals(p)) result.add((Point)point.clone());

		return result;
	}
	
	/**
	 * Meme fonction que neighbor mais cette fois-ci on passe des PointDeg en parametre
	 * @param p
	 * @param vertices
	 * @return
	 */
	public static ArrayList<PointDeg> neighbor2(PointDeg p, ArrayList<PointDeg> vertices){
		ArrayList<PointDeg> result = new ArrayList<PointDeg>();

		for (PointDeg point:vertices) if (point.distance(p)<100 && !point.equals(p)) result.add(point.clone());

		return result;
	}


	/**
	 * Enleve les points qui ont un degre <= 1
	 * @param points
	 * @return
	 */
	public static ArrayList<PointDeg> cleanup(ArrayList<PointDeg> points){
		ArrayList<PointDeg> res = new ArrayList<PointDeg>();
		// On met a jour les degres des points.
		for(int i = 0; i < points.size(); i++){
			points.set(i, new PointDeg(new Point(points.get(i).x, points.get(i).y), neighbor2(points.get(i), points).size()));
			
		}
		
		for(PointDeg p : points){
			if(p.degree > 1)
				res.add(p);
		}

		return res;
	}

	/**
	 * Algorithme Bafna-Berman-Fujito  
	 * @param data
	 * @return
	 */
	public static ArrayList<Point> calculFVS(ArrayList<Point> data){
		ArrayList<PointDeg> points =  new ArrayList<PointDeg>();
		for(Point p : data){ // Conversion point-> pointDeg pour la suite
			points.add(new PointDeg(p, data));
		}
		ArrayList<Point> ret = new ArrayList<Point>();
		ArrayList<PointDeg> f = new ArrayList<PointDeg>(); // Correspond a F dans l'algorithme de l'article
		ArrayList<PointDeg> g = new ArrayList<PointDeg>();
		Stack<PointDeg> s = new Stack<PointDeg>(); // Pile STACK
		PointDeg u = new PointDeg();
		Cycle c; // Utilise pour le calcul du cycle semi-disjoint
		double gamma;

		points = cleanup(points);
		
		while(!points.isEmpty()){
			c = new Cycle(points);
			if( (g = c.calculSdCycle()) != null){ // null = pas de cycle semi-disjoint
				gamma = minWeight(g); // min des w(u) du cycle semi-disjoint g 
				points = changeWeight(points, g, gamma);  // set w(u) <- w(u) - gamma
			}
			else{
				gamma = minWeight2(points); // min des w(u)/(d(u) - 1) du graphe
				points = changeWeight2(points, gamma); // set w(u) <- w(u) - gamma(d(u) - 1)
			}

			for(int i = 0; i < points.size(); i++){
				if(points.get(i).getWeight() == 0){
					f.add(points.get(i));
					s.push(points.get(i));
					points.remove(i);
					i--; // Pour s'assurer qu'on parcourt bien tous les points du graphe
				}
			}
			points = cleanup(points);
		}

		while(!s.isEmpty()){		
			u = s.pop(); 
			f.remove(u);
			 // Conversion PointDeg -> Point pour utiliser la fonction isValide
			ret = new ArrayList<Point>();
			for(PointDeg re : f){
				ret.add(new Point(re.x, re.y));
			}
			//Si F -{u} n'est pas un FVS dans le graphe original, on ajoute le point precedemment supprime
			if(!isValide(data, ret)){
				f.add(u);
				ret.add(new Point(u.x, u.y));
			}
		}
		// On retourne le FVS
		return ret;
	}

	/**
	 * Retourne le poids minimal des points passe en parametre
	 * @param points
	 * @return
	 */
	public static double minWeight(ArrayList<PointDeg> points){
		double min = Double.MAX_VALUE;

		for(PointDeg p : points){
			if(p.weight < min){
				min = p.weight;
			}
		}

		return min;
	}

	/**
	 * Retourne un double contenant le w(u)/(d(u) - 1) d'un point u minimal des points passes en parametre
	 * @param points
	 * @return
	 */
	public static double minWeight2(ArrayList<PointDeg> points){
		double min = Double.MAX_VALUE;	

		for(PointDeg p : points){
			if((p.weight/(p.degree - 1.0)) < min){
				min = p.weight/(p.degree - 1.0);
			}
		}

		return min;
	}
	
	/**
	 * Modifie le poids de chaque point dans le cycle semi-disjoint (correspond a w(u)<- w(u) - gamma pour
	 * tout u in V(C)
	 * @param src graphe
	 * @param sscycle cycle semi-disjoint
	 * @param gamma
	 * @return
	 */
	public static ArrayList<PointDeg> changeWeight(ArrayList<PointDeg> src, ArrayList<PointDeg> sscycle, double gamma){
		for(PointDeg p : src){
			for(PointDeg q : sscycle){
				if(p.equals(q)){
					p.setWeight(p.getWeight() - gamma);
					break;
				}
			}
		}

		return src;
	}

	/**
	 * Modifie le poids de chaque point du graphe. (correspond a w(u) <- w(u) - gamma(d(u) - 1)
	 * @param src Point du graphe
	 * @param gamma
	 * @return
	 */
	public static ArrayList<PointDeg> changeWeight2(ArrayList<PointDeg> src, double gamma){
		for(PointDeg p : src){
			p.setWeight(p.getWeight() - gamma * (p.getDegree() - 1));
		}

		return src;
	}
}
