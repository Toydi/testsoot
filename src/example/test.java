package example;

public class test {
	
	public int getMax(int a,int b,int c){
		if(a>b){
			if(a>c){
				return a;
			}
			return c;
		}
		//test
		else if(b>c){
			return b;
		}
		else 
			return c;
	}

}