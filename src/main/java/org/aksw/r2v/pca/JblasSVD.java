package org.aksw.r2v.pca;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jblas.DoubleMatrix;
import org.jblas.Singular;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class JblasSVD {
	
	protected static Logger logger = LogManager.getLogger(JblasSVD.class);

	public static void main(String[] args) {

		int d = 5;
		DoubleMatrix A = DoubleMatrix.randn(d, d);
		
		for(int dim=1; dim<d; dim++) {
			DoubleMatrix C = pca(A, dim);
			visual("C(dim="+dim+")", C);
			logger.info("====================");
		}

	}
	
	private static DoubleMatrix centerData(DoubleMatrix A) {
		
		DoubleMatrix means = A.columnMeans();
		
		for(int i=0; i<A.rows; i++)
			for(int j=0; j<A.columns; j++)
				A.put(i, j, A.get(i, j) - means.get(j));
		
		return A;
	}
	
	public static DoubleMatrix pca(double[][] A, int n) {
		
		return pca(new DoubleMatrix(A), n);
		
	}
	
	/**
	 * 
	 * 
	 * @param A
	 * @param dim
	 * @return
	 */
	public static DoubleMatrix pca(DoubleMatrix A, int dim) {
		
		A = centerData(A);
		visual("A", A);
		
		DoubleMatrix[] usv = Singular.fullSVD(A);
		DoubleMatrix U = usv[0];
		DoubleMatrix S = usv[1];
		
		// 
		DoubleMatrix Uk = new DoubleMatrix(U.rows, dim);
		for(int i=0; i<dim; i++)
			Uk.putColumn(i, U.getColumn(i));
		visual("Uk", Uk);
		
		// build S matrix
		DoubleMatrix Sm = new DoubleMatrix(dim, dim);
		for (int i = 0; i < dim; i++) {
			Sm.put(i, i, S.get(i));
		}
		visual("Sm", Sm);
		
		// calculate principal component matrix...
		DoubleMatrix B = Uk.mmul(Sm);
		visual("B", B);
		
		return B;
		
	}

	public static void visual(String name, Object o) {
		
		new File("etc/").mkdir();
		logger.info("Saving '"+name+"' to file...");
		
		try {
			PrintWriter pw = new PrintWriter(new File("etc/" + name + ".txt"));
			for(String str : o.toString().replaceAll(", ", "\t").split(";"))
				pw.println(str);
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
