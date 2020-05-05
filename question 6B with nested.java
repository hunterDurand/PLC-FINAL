package whatup;
import java.util.Scanner;

public class Max_min_nested {
	public static void main(String[] args) {
		// TODO code application logic here
		int a,b,c;
		int max=0,min=0,mid=0;
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter three number: ");
		a=sc.nextInt();
		b=sc.nextInt();
		c=sc.nextInt();
		  
		if(a>b)
		{
		if(a>c)
		{
		if(b>c)
		{
		max=a;
		min=c;
		mid=b;
		}
		else
		{
		max=a;
		min=b;
		mid=c;
		}
		}
		else
		{
		max=c;
		min=b;
		mid=a;
		  
		  
		}
		  
		}
		else
		{
		if(b<c)
		{
		max=c;
		min=a;
		mid=b;

		}
		else
		{
		if(a<c)
		{
		max=b;
		min=a;
		mid=c;  
		}
		else
		{
		max=b;
		min=c;
		mid=a;
		}
		}
		  
		}
		  
		System.out.println("Max is "+ max);
		System.out.println("Min is "+ min);
		System.out.println("Mid is "+ mid);
		}
}
