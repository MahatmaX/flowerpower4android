package de.fp4a.timeseries;

/*
Copyright 2007 Creare Inc.

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License.
*/

//
// 2D Polyline simplification via Douglas-Peucker.
// (See "http://geometryalgorithms.com/Archive/algorithm_0205/" for details.)
// Removes redundant points from a high-resolution line.
//

/**
  * Two dimensional polyline simplification via Douglas-Peucker.
  * Removes redundant points from a high-resolution line.
  */
public class TimeSeriesLineSimplification
{
	private TimeSeriesLineSimplification() { }

	/**
	  * Simplification function.  If a <code>tol</code> value of 0.0 is 
	  *  specified, the actual tolerance will be:
	  * <p><code>1e-3 * sqrt( (max(x) - min(x))<sup>2</sup> 
	  *  + (max(y) - min(y))<sup>2</sup>.</code></p>
	  * <p>@return An array of length x.length, which indicates whether
	  *  to keep (true) or discard (false) each vertex.
	  * @throws IllegalArgumentException if the inputs are invalid.
	  * @throws NullPointerException if any input is null.
	  */
	public static boolean[] simplify(Long[] x, Float[] y, double tol)
	{
		if (x.length != y.length) throw new IllegalArgumentException(
				"Input arrays must be of the same length.");
		
		if (tol < 0 || Double.isNaN(tol)) throw new IllegalArgumentException(
				"Tolerance must be positive or zero.");
		
		double tol2;
		if (tol == 0) { // calculate from inputs
			double minx = Double.POSITIVE_INFINITY,
				maxx = Double.NEGATIVE_INFINITY,
				miny = Double.POSITIVE_INFINITY,
				maxy = Double.NEGATIVE_INFINITY;
				
			for (int ii = 0; ii < x.length; ++ii) {
				double xv = x[ii], yv = y[ii];
				// JPW 03/13/2006: don't consider points
				//                 which include a NaN
				if (validDataPoint(xv, yv)) {
				    if (xv < minx) minx = xv;
				    if (xv > maxx) maxx = xv;
				    if (yv < miny) miny = yv;
				    if (yv > maxy) maxy = yv;
				}
			}
			maxx -= minx; maxy -= miny;
			tol2 = 0.001 * ( maxx * maxx + maxy * maxy);
		} else tol2 = tol * tol;
		
		boolean[] out = new boolean[x.length];
		
		// JPW 03/13/2006: discard vertices which are NaN
		// Old way of doing it...we just assume the first and last
		// points are valid data
		// out[0] = true;
		// out[out.length-1] = true;
		// simpimp(tol2, x, y, 0, x.length-1, out);
		int startIndex = -1;
		int endIndex = -1;
		// Find the first valid data point
		for (int ii = 0; ii < x.length; ++ii) {
		    if (validDataPoint(x[ii], y[ii])) {
			// This is our starting point
			out[ii] = true;
			startIndex = ii;
			break;
		    }
		}
		// Find the last valid data point
		for (int ii = (x.length - 1); ii >= 0; --ii) {
		    if (validDataPoint(x[ii], y[ii])) {
			// This is our ending point
			out[ii] = true;
			endIndex = ii;
			break;
		    }
		}
		if (startIndex == -1) {
		    // All the data must be NaN; just return the boolean array
		    // with all values false
		    return out;
		} else if ( (startIndex == endIndex) || (endIndex == (startIndex + 1)) ) {
		    // Either there is just 1 valid data point, or else there
		    // are only 2 valid data points which are adjacent to each
		    // other.  In either case, we're done!
		    return out;
		}
		
		simpimp(tol2, x, y, startIndex, endIndex, out);
		
		return out;
	}
	
	/**
	  * Marks vertices that are part of the simplified polyline
	  * for approximating the polyline subchain v[j] to v[k].
	  *
	  * Note that this is simp-imp, not sim-pimp.
	  *
	  *<p> @param tol2 tolerance squared.
	 */
	private static void simpimp(double tol2, Long[] x, Float[] y,
			int j, int k, boolean[] out)
	{
		double maxd2 = 0;       // dist to farthest vertex
		int maxi = j;        // index of farthest vertex
		
		for (int i = j+1; i <= k; ++i) {
			// JPW 03/13/2006: don't consider NaN vertices
			if (validDataPoint(x[i], y[i])) {
			    double dv2 = //pointsegdist(line(i,:), line(j,:), line(k,:));
				pointsegdist(x[i], y[i], x[j], y[j], x[k], y[k]);
			    if (dv2 > maxd2) {
				maxi = i;
				maxd2 = dv2;
			    }
			}
		}
		if (maxd2 > tol2) {
			out[maxi] = true;
			// Simplify two subsections:
			simpimp(tol2, x, y, j, maxi, out);
			simpimp(tol2, x, y, maxi, k, out);
		}
	}
	
	/**
	  * Squared dist between point P and line seg formed by P0, P1.
	  */
	private static double pointsegdist(
			double x, double y,
			double x0, double y0,
			double x1, double y1)
	{
		// distance( Point P, Segment P0:P1 )
		//v = P1 - P0;
		double vx = x1 - x0, vy = y1 - y0,
			wx = x - x0, wy = y - y0;
		
		//c1 = dot(w, v);
		double c1 = wx*vx + wy*vy;
		if (c1 <= 0) return distSq(x, y, x0, y0);
		
		//c2 = dot(v, v);
		double c2 = vx*vx + vy*vy;
		if (c2 <= c1) return distSq(x, y, x1, y1);
		double b = c1 / c2;
		//Pb = P0 + b*v;
		double Pbx = x0 + b*vx, Pby = y0 + b*vy;
		return distSq(x, y, Pbx, Pby);
	}
	
	private static double distSq(double x1, double y1, double x2, double y2)
	{
		x1 -= x2;  y1 -= y2;
		return x1*x1 + y1*y1;
	}
	
	/**
	 * Is the given data point valid?  It will be valid if neither the x
	 * nor the y data are NaN
	 */
	 private static boolean validDataPoint(double x, double y) {
	     if ( (!Double.isNaN(x)) && (!Double.isNaN(y)) ) {
		 return true;
	     }
	     return false;
	 }
}

