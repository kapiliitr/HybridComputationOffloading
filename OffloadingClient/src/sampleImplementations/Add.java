package sampleImplementations;


import exchangeTemplates.CodeClass;
import exchangeTemplates.DataClass;

public class Add extends CodeClass {
	int a; 
	int b;
	
	@Override
	public void initialize(DataClass input) {
		// TODO Auto-generated method stub
		a = input.getIntegerParamters()[0];
		b = input.getIntegerParamters()[1];
		
	}
	
	@Override
	public void run(DataClass output) {
		// TODO Auto-generated method stub
		output.setIntegerParamters(new int[] {a+b});
		
	}


}
