import java.util.Scanner;
class Simple{
public static void main(String[] args)
{
   int i,j,n=4,index=-1;
   int[][] x = {{0, 1, 1, 1},{0, 0, 0, 0},{1, 1, 1, 1},{4, 2, 1, 6}}; //initilize array here
   for(i=0;i<n;i++)//this is for loop for check it
   {
       for(j=0;j<n;j++)//this is for loop for it j
       {
           if(x[i][j]!=0)//break if it non zero
           break;
           else if(j==n-1 && x[i][j]==0)//break if it last in row in n and still zero
           {
           index=i;
           break;
           }
       }
   }
   if (index==-1)
   System.out.println("No row found with all zero");//print if no all zero found
   else
   System.out.println("Row Number is "+(index+1));//print index
}
}