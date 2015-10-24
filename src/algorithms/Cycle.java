package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe de calcul du Cycle semi-disjoint. 
 * @author Eric
 *
 */
public class Cycle {
	 // On attribut a chaque point du graphe un booleen pour le parcours en profondeur?
	public Map<PointDeg, Boolean> marked;
	public ArrayList<PointDeg> points; // Contient tous les points du graphe

	public Cycle(ArrayList<PointDeg> points){
		this.points = points;
		marked = new HashMap<PointDeg, Boolean>();
		for(PointDeg p : points){
			marked.put(p, false);
		}
	}

	/**
	 * Renvoie s'il existe un cycle semi-disjoint des points du graphe. S'il n'y a pas de cycle, renvoie null
	 * @return la liste des points formant un cycle semi-disjoint
	 * 
	 */
	public ArrayList<PointDeg> calculSdCycle(){
		ArrayList<PointDeg> res = new ArrayList<PointDeg>();
		for(PointDeg p : points)
			if(!marked.get(p)) // On s'assure qu'on a parcouru tous les points du graphe
				if( (res = calculSDCycle_rec(p, p, new ArrayList<PointDeg>())) != null)
					return res;
		return null;
	}

	/**
	 * Renvoie un cycle semi-disjoint des points du graphe. S'il n'y a pas de cycle, renvoie null
	 * On utilise un parcours en profondeur classique tout en conservant le chemin dans une ArrayList
	 * passe en parametre
	 * @param prev Point precedemmment parcouru
	 * @param current
	 * @param chemin ArrayList utilise pour stocker les points pendant la recursion
	 * @return 
	 */
	public ArrayList<PointDeg> calculSDCycle_rec(PointDeg prev, PointDeg current, ArrayList<PointDeg> chemin){
		marked.replace(current, true); // On marque le point current
		chemin = PointDeg.cloneArrayList(chemin);
		chemin.add(current); 
		
		ArrayList<PointDeg> voisins = Evaluation.neighbor2(current, points); // On stocke les voisins du point current
		for(PointDeg p : voisins){
			if(!marked.get(p)){
				ArrayList<PointDeg> res = new ArrayList<PointDeg>();
				// Si on a trouve un cycle pendant la recursion, on arrete et on le retourne
				if( (res = calculSDCycle_rec(current, p, chemin)) != null) return res;
			}
			else{ // Si on tombe sur un point deja marque et qu'il est != prev
				if(!p.equals(prev)){
					while(!chemin.get(0).equals(p)){ // On enleve les points en dehors du cycle
						chemin.remove(0);
						if(chemin.isEmpty())
							return null;
					}
					boolean test = false;

					 // On teste si tous les points du cycle ont un degre = 2 sauf po 
					for(int i = 0; i < chemin.size(); i++){
						if(chemin.get(i).degree != 2)
							if(!test) // exception
								test = true;
							else // Si on a deux fois l'exception, ce n'est pas un cycle semi-disjoint. On 
								//retourne donc null.
								return null;
					}
					return chemin;
				}
			}
		}
		return null;
	}
}
