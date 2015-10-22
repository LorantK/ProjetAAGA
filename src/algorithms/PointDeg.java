 package algorithms;

import java.awt.Point;
import java.util.ArrayList;

/**
 * Classe PointDeg heritant de point. Elle contient le degre et le poids de ce point
 * @author Eric, Kevin
 *
 */
public class PointDeg extends Point{
	public int degree;
	public double weight = 1;

	public PointDeg(Point a, ArrayList<Point> points){
		super(a);
		degree = Evaluation.neighbor(a, points).size();
	}
	
	public PointDeg(Point a, int degree){
		super(a);
		this.degree = degree;
	}

	public PointDeg(int x, int y){
		super(x, y);
		degree = 0;
	}
	
	public PointDeg(){
		super();
	}

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public PointDeg clone(){
		PointDeg p = new PointDeg(this.x, this.y);
		p.setDegree(this.degree);
		p.setWeight(this.weight);
		return p;
	}

	@Override
	public boolean equals(Object obj) {
		if( obj instanceof PointDeg){
			PointDeg o = (PointDeg) obj;
			return this.x == o.x && this.y == o.y && this.weight == o.weight && this.degree == o.degree;
		}
		
		return false;
	}
	
	/**
	 * Retourne le clone de l'arraylist passe en parametre
	 * @param data
	 * @return
	 */
	public static ArrayList<PointDeg> cloneArrayList(ArrayList<PointDeg> data){
		ArrayList<PointDeg> res = new ArrayList<PointDeg>();
		for(PointDeg p : data){
			res.add(p.clone());
		}
		return res;
	}

}
