/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package appmain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Formatter;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 *
 * @author pekardy.milan
 */
public class AppMain {
    
    private final String input;
    private final String output;
    
    public AppMain(){
        
        input = "input.csv";
        
        Calendar c = Calendar.getInstance();
        output = "IMPORT_" + c.get(Calendar.YEAR) + (c.get(Calendar.MONTH) + 1) + c.get(Calendar.DATE) + "_" + c.getTimeInMillis() + ".TXT";
        
    }
    
    public static void main(String[] args){
        
        try {
             UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            
        }
        
        AppMain main = new AppMain();
        
        main.processFiles();
        
    }
    
    public void processFiles(){
        Formatter out = null;
        try{
            File f = new File(input);
            if(f.exists()){
                out = new Formatter(new File(output));
                BufferedReader reader = new BufferedReader(new FileReader(f));
                String line;
                int count = 0;
                while((line = reader.readLine()) != null){
                    if(count != 0)
                        out.format(convertLine(line));
                    count++;
                    //System.out.println(line);
                }
                JOptionPane.showMessageDialog(null, "Az import fájl előállt!", "Információ", JOptionPane.INFORMATION_MESSAGE);
            }
            else{
                JOptionPane.showMessageDialog(null, "Nincs meg az input fájl!", "Figyelmeztetés", JOptionPane.WARNING_MESSAGE);
            }
            
            
            
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "Hiba a feldolgozás közben!\n" + e + "\n" + e.fillInStackTrace(), "Hiba", JOptionPane.WARNING_MESSAGE);
            new File(output).deleteOnExit();
            
            System.exit(1);
        }
        finally{
            if(out != null){
                out.flush();
                out.close();
            }
        }
    }
    
    private String convertLine(String line){
        StringBuilder newLine = new StringBuilder();
        String[] parts = line.split(";");
        if(!parts[5].contains("Átvezetés egy ügyfél számlái között") && 
          ((parts.length == 7 && parts[2].equals("HUF")) || 
                (parts.length == 6 && parts[2].equals("HUF") && (parts[5].equals("Befizetés") || 
                parts[5].equals("Jóváírás főkönyvvel szemben"))))){
            String default1 = "0" + getSpaces(9);
            String default2 = "AAAA-0000" + getSpaces(1);
            String default3 = "000";
            String inAccNum = parts.length == 7 ? parts[6].replace("-", "") : getSpaces(24);
            String accNum = inAccNum + getSpaces(24 - inAccNum.length());
            String partner = parts[3].length() > 32 ? parts[3].substring(0, 32) : parts[3] + getSpaces(32 - parts[3].length());
            String default4 = "0000";
            String defaultAccNum = "****************        ";
            String defaultPartner = "********************************";
            String date = (parts[0].trim().equals("") || !parts[0].trim().matches("\\d{4}/\\d{2}/\\d{2}")) ? getSpaces(8) : parts[0].replace("/", "");
            String inAmount = parts[1].substring(1) + ".00";
            String amount = getSpaces(15 - inAmount.length()) + inAmount;
            String currency = parts[2];
            String comment1 = "", comment2 = "", comment3 = "";
            String inComment = parts[4].trim();
            if(inComment.length() <= 32){
                comment1 = inComment + getSpaces(32 - inComment.length());
                comment2 = getSpaces(32);
                comment3 = getSpaces(32);
            }
            else if(inComment.length() <= 64){
                comment1 = inComment.substring(0, 32);
                comment2 = inComment.substring(32, inComment.length()) + getSpaces(64 - inComment.length());
                comment3 = getSpaces(32);
            }
            else if(inComment.length() <= 96){
                comment1 = inComment.substring(0, 32);
                comment2 = inComment.substring(32, 64);
                comment3 = inComment.substring(64, inComment.length()) + getSpaces(96 - inComment.length());
            }
            else{
                comment1 = inComment.substring(0, 96);
            }
            newLine.append(default1).append(default2).append(default3).append(accNum).append(partner).append(default4).append(defaultAccNum).append(defaultPartner)
                    .append(date).append(amount).append(currency).append(comment1).append(comment2).append(comment3);
            newLine.append(getSpaces(291 - newLine.length())).append("\r\n");
        }
        return newLine.toString();
    }
    
    private String getSpaces(int n){
        StringBuilder spaces = new StringBuilder(n);
        for(int i = 0; i < n; i++){
            spaces.append(" ");
        }
        return spaces.toString();
    }
    
}
