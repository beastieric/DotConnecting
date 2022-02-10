import java.util.*;
import java.io.*;

public class Main {
	
	String filename;
	double error, maxSlope, maxD;
	
	public Main(String fname, double e, double mSlope, double mD) {
		filename = fname;
		error = e;
		maxSlope = mSlope;
		maxD = mD;
	}
	
	public void run() throws FileNotFoundException, IOException{
		//String filename = "PB7_TOPO";
		Scanner sc = new Scanner(new File(/*"input/" + */filename + ".csv"));
		sc.useDelimiter(",|\\n");
		List<Row> rows = new ArrayList<Row>();
		while(sc.hasNext()) {
			rows.add(new Row(sc.nextInt(), sc.nextDouble(), sc.nextDouble(), sc.nextDouble(), sc.next()));
		}

		List<Row> points = new ArrayList<Row>();
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(/*"input/" + */filename + "_OUT.csv")));
		boolean start = true;
		for(Row r : rows) {
			points.add(r);
			if(r.description.trim().equals("_END")) {
				if(start) {
					start = false;
				}else {
					start = true;
					for(Row point : solve(points)) {
						out.println(point);
					}
					
					points = new ArrayList<Row>();
				}
			}
		}
		
		
		out.close();
		sc.close();
	}
	
	public List<Row> solve(List<Row> points){
		//double error = 1;
		Collections.sort(points, new Comparator<Row>() {

			@Override
			public int compare(Main.Row o1, Main.Row o2) {
				if(o1.easting < o2.easting) {
					return -1;
				}else if(o1.easting > o2.easting) {
					return 1;
				}else if(o1.northing > o2.northing){
					return 1;
				}else {
					return -1;
				}
			}
			
		});
		int motorI = 0;
		for(Row r : points) {
			if(r.description.equals("_MOTOR_PILE")) {
				break;
			}
			motorI++;
		}
		List<Row> ret = new ArrayList<Row>();
		for(int j = 1; j <= 10; j++) {
			double[] regression = regression(points, points.get(motorI).northing, points.get(motorI).elevation, maxSlope*0.1*j);
			ret = new ArrayList<Row>();
			for(int i = 0; i < points.size(); i++) {
				double ele = regression[0]*points.get(i).northing + regression[1];
				ele = Math.min(ele, points.get(i).elevation+error);
				ele = Math.max(ele, points.get(i).elevation-error);
				ret.add(new Row(points.get(i).point, points.get(i).northing, points.get(i).easting, ele, points.get(i).description));
			}
			
			if(!fixDeflections(ret, points, motorI, 0.011, 0.01)) {
				if(!fixDeflections(ret, points, motorI, 0.009, 0.011)) {
					if(!fixDeflections(ret, points, motorI, 0.01, 0.01)) {
						if(!fixDeflections(ret, points, motorI, 0.01, 0.011)) {
							if(!fixDeflections(ret, points, motorI, 0.01, 0.009)) {
								if(j == 10) {
									System.out.println("failed with " + points.get(0).point);
								}
								continue;
							}
						}
					}
				}
			}
			break;
		}
		
		return ret;
	}
	
	public boolean fixDeflections(List<Row> ret, List<Row> points, int motorI, double mid, double sides) {
		for(int iter = 0; iter < 10000; iter++) {
			double maxDeflection = 0;
			int maxDefI = -1;
			//change northing based on easting
			for(int i = motorI-1; i >= 0; i--) {
				double s2 = (points.get(i+1).elevation-points.get(i).elevation)*(points.get(i+1).elevation-points.get(i).elevation)
						+ (points.get(i+1).northing-points.get(i).northing)*(points.get(i+1).northing-points.get(i).northing);
				ret.get(i).northing = ret.get(i+1).northing - Math.sqrt(s2-(ret.get(i).elevation-points.get(i).elevation)*(ret.get(i).elevation-points.get(i).elevation));
			}
			for(int i = motorI+1; i < ret.size(); i++) {
				double s2 = (points.get(i-1).elevation-points.get(i).elevation)*(points.get(i-1).elevation-points.get(i).elevation)
						+ (points.get(i-1).northing-points.get(i).northing)*(points.get(i-1).northing-points.get(i).northing);
				ret.get(i).northing = ret.get(i-1).northing + Math.sqrt(s2-(ret.get(i).elevation-points.get(i).elevation)*(ret.get(i).elevation-points.get(i).elevation));
			}
			for(int i = 1; i < ret.size()-1; i++) {
				double deflection = slope(ret.get(i-1), ret.get(i))-slope(ret.get(i), ret.get(i+1));
				if(Math.abs(deflection) > Math.abs(maxDeflection) && Math.abs(deflection) > maxD) {
					maxDeflection = deflection;
					maxDefI = i;
				}
			}
			if(maxDefI == -1) {				
				return true;
			}
			//facing down
			if(maxDeflection > 0) {
				ret.get(maxDefI).elevation-=mid;
				ret.get(maxDefI).elevation = Math.max(ret.get(maxDefI).elevation, points.get(maxDefI).elevation-error);
				ret.get(maxDefI+1).elevation+=sides;
				ret.get(maxDefI+1).elevation = Math.min(ret.get(maxDefI+1).elevation, points.get(maxDefI+1).elevation+error);
				ret.get(maxDefI-1).elevation+=sides;
				ret.get(maxDefI-1).elevation = Math.min(ret.get(maxDefI-1).elevation, points.get(maxDefI-1).elevation+error);
			}else {
				ret.get(maxDefI).elevation+=mid;
				ret.get(maxDefI).elevation = Math.min(ret.get(maxDefI).elevation, points.get(maxDefI).elevation+error);
				ret.get(maxDefI+1).elevation-=sides;
				ret.get(maxDefI+1).elevation = Math.max(ret.get(maxDefI+1).elevation, points.get(maxDefI+1).elevation-error);
				ret.get(maxDefI-1).elevation-=mid;
				ret.get(maxDefI-1).elevation = Math.max(ret.get(maxDefI-1).elevation, points.get(maxDefI-1).elevation-error);
			}
			ret.get(motorI).elevation = points.get(motorI).elevation;
		}
		return false;
	}
	
	public double slope(Row r1, Row r2) {
		return (r1.elevation-r2.elevation)/(r1.northing-r2.northing)*100;
	}
	
	public double[] regression(List<Row> points, double h, double k, double maxSlope) {
		double s1 = 0, s2 = 0, s3 = 0, sumXY = 0, sumY = 0;
		//double maxSlope = 0.03;
		for(Row point : points) {
			s3 += point.northing*point.northing;
			s2 += point.northing;
			sumY += point.elevation;
			sumXY += point.northing*point.elevation;
		}
		if(h != 0) {
			s1 = (h*k*s2-k*s3-h*h*sumY+h*sumXY)/(2*h*s2-s3-h*h*points.size());
			double slope = Math.max(Math.min((k-s1)/h, maxSlope), -maxSlope);
			return new double[]{slope, k-h*slope};
		}else {
			double slope = Math.max(Math.min(sumXY/s3, maxSlope), -maxSlope);
			return new double[] {slope, k-h*slope};
		}
	}
	
	class Row {
		int point;
		double northing, easting, elevation;
		String description;
		
		public Row(int p, double n, double ea, double el, String d) {
			point = p;
			northing = n;
			easting = ea;
			elevation = el;
			description = d.trim();
		}
		
		public String toString() {
			return point + "," + northing + "," + easting + "," + elevation + "," + description;
		}
	}
	
}
