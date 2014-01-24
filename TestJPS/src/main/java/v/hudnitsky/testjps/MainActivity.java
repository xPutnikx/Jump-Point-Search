package v.hudnitsky.testjps;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.io.Files;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import v.hudnitsky.testjps.jps2.Grid;
import v.hudnitsky.testjps.jps2.JPS;
import v.hudnitsky.testjps.jps2.Node;

public class MainActivity extends Activity {

    private TextView txvLogs;
    private EditText edtxvWVal;
    private EditText edtxvHVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnTestJPS = (Button) findViewById(R.id.test_jps);
        Button btnCalcAll = (Button) findViewById(R.id.btn_calc_all);
        txvLogs = (TextView) findViewById(R.id.logs);
        edtxvHVal = (EditText) findViewById(R.id.h_val);
        edtxvWVal = (EditText) findViewById(R.id.w_val);
        btnTestJPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hValStr = edtxvHVal.getText().toString();
                String wValStr = edtxvWVal.getText().toString();
                if(!hValStr.isEmpty() && !wValStr.isEmpty()){
                    String stringMatrix = readMatrixFromFile();
                    try {
                        JSONArray  jsonArray = new JSONArray(stringMatrix);
                        final int h = jsonArray.length();
                        final int w = jsonArray.getJSONArray(0).length();
                        final int[][] matrix = new int[h][w];
                        for(int i = 0; i < jsonArray.length(); i++){
                            JSONArray innerArray = jsonArray.getJSONArray(i);
                            for(int k = 0; k < innerArray.length(); k++){
                                matrix[i][k] = innerArray.getInt(k);
                            }
                        }
                        Grid grid = new Grid(h,w,matrix);
                        JPSTask jpsTask = new JPSTask(grid);
                        int hVal = Integer.valueOf(edtxvHVal.getText().toString());
                        int wVal = Integer.valueOf(edtxvWVal.getText().toString());
                        jpsTask.execute(1,1,hVal,wVal);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        btnCalcAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stringMatrix = readMatrixFromFile();
                try {
                    JSONArray  jsonArray = new JSONArray(stringMatrix);
                    final int h = jsonArray.length();
                    final int w = jsonArray.getJSONArray(0).length();
                    final int[][] matrix = new int[h][w];
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONArray innerArray = jsonArray.getJSONArray(i);
                        for(int k = 0; k < innerArray.length(); k++){
                            matrix[i][k] = innerArray.getInt(k);
                        }
                    }
//                  for(int eW = 0; eW < w; eW++){
//                      for(int eH = 0; eH < h; eH++){
                            for(int hH = 2; hH < 15; hH++){
                                for(int hW = 9; hW < 15; hW++){
                                    JPSTask jpsTask = new JPSTask(new Grid(h,w,matrix));
                                    jpsTask.execute(1,1,hH,hW);
                                }
                            }
//                      }
//                  }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Button btnClear = (Button)findViewById(R.id.btn_clear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txvLogs.setText("");
            }
        });
    }

    private String readMatrixFromFile(){
        try {
            InputStream inputStream = getAssets().open("DM_matrix.txt");
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void writeResultsToFile(String result){
        String globalPath = Environment.getExternalStorageDirectory().toString();
        File file = new File(globalPath,"DM_results.txt");
        try {
            Files.write(result.getBytes(),file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class JPSTask extends AsyncTask<Integer,Void,String>{
        private final Grid grid;

        JPSTask(Grid grid) {
            this.grid = grid;
        }

        @Override
        protected String doInBackground(Integer... params) {
            int eW = params[0];
            int eH = params[1];
            int hH = params[2];
            int hW = params[3];
            StringBuilder resultBuilder = new StringBuilder();
            if(eW!=hW && eH!=hH){
                List<Node> nodes = new JPS().search(new Node(eW, eH), new Node(hH, hW), grid);
                if(nodes.size()>0){
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        String value = mapper.writeValueAsString(nodes);
                        resultBuilder.append(value);
                        resultBuilder.append(" eH:").append(eH);
                        resultBuilder.append(" eW:").append(eW);
                        resultBuilder.append(" hH:").append(hH);
                        resultBuilder.append(" hW:").append(hW);
                        resultBuilder.append("\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            }
            return resultBuilder.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            txvLogs.setText(txvLogs.getText()+s);
            super.onPostExecute(s);
        }
    }
}
