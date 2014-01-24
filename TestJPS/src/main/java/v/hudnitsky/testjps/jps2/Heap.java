package v.hudnitsky.testjps.jps2;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import java.util.Iterator;
import java.util.LinkedList;

//This is actually just a priority queue, should be a heap when implemented for game use
//in order to improve runtime for adding an element
/**
 *  @author v.hudnitsky
 */

public class Heap {
	LinkedList<float[]> list;
	Iterator<float[]> listit;
	
	public Heap(){
		list = new LinkedList<float[]>();
	}
	
	public void add(float[] newXY){
		if (list.size()>0){
			listit = list.iterator();
			float[] tmp;
			int count = 0;
			while (true){
				tmp = listit.next();
				if (tmp[2]>newXY[2]){
					list.add(count, newXY);
					break;
				}
				else{
					count++;
				}
				if (!listit.hasNext()){
					list.add(count, newXY);
					break;
				}
                Log.d("JPS", "HEAP ADD : " + count);
			}
		}
		else{
			list.add(newXY);
		}
	}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public float[] pop(){
		try{
			return list.pop();
		}
		catch(Exception e){
			return null;
		}
	}

	public int getSize(){
		return list.size();
	}
}
