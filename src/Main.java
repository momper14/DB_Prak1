import java.io.*;
import java.util.*;

public class Main {
    
    private static ArrayList<ISAM> isamList = new ArrayList<ISAM>();
    private static ISAM isam = new ISAM();
  
    private static long pointerTopDAT;
    private static String readline;
    private static String[] split;
    private static StringBuffer oneLine = new StringBuffer();

    public static void main(String args[]) throws IOException, ClassNotFoundException {
        
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        RandomAccessFile raf1 = new RandomAccessFile("ARTIKEL.csv", "rw");
        RandomAccessFile raf2 = new RandomAccessFile("ARTIKEL.idx", "rw");

        int choice;
        String[] split2;
        //cache = raf1.getFilePointer();
        while(null!=(readline = raf2.readLine())){
            split = readline.split(";");
            for (String split1 : split) {
                split2 = split1.split(",");
                isam = new ISAM((Integer.parseInt(split2[0])),(Integer.parseInt(split2[1])));
                isamList.add(isam);
            }
        }
        
        pointerTopDAT = raf1.length();
        raf2.seek(raf2.length());
        
        do {
            System.out.println("Menü:\n\t1 - neue Daten eingeben\n\t2 - Sequentieller Zugriff\n\t3 - lesender Direktzugriff\n\t0 - Programm beenden");
            choice = Integer.parseInt(in.readLine());

            switch(choice) {
                case 1: einlesenDatensatz(raf1, raf2);
                break;
                case 2: sequentiellerZugriff(raf1);
                break;
                case 3: lesenderDirekzugriff(raf1);
                break;
                case 0: beenden(raf1, raf2);
                break;
                default: choice = 99; System.out.println("Bitte eine korrekte Zahl eingeben.");
            }
        } while(choice!=0);
    }

    public static void einlesenDatensatz(RandomAccessFile raf1,RandomAccessFile raf2) throws IOException, ClassNotFoundException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        int artnr;
        String artbez, mge;
        Double preis;
        int steu;
        boolean ungueltig;
        
        do{
            ungueltig = false;
            System.out.println("Bitte geben Sie die Artikelnummer ein.");
            artnr = Integer.parseInt(in.readLine());
            for(ISAM x : isamList){
                if(x.getArtnr() == artnr)
                    ungueltig = true;
            }
            if(ungueltig)
                System.out.println("Artikelnummer bereits vergeben");
        }while(ungueltig);
        
        
        System.out.println("Bitte geben Sie die Artikelbezeichnung ein.");
        artbez = in.readLine();
        System.out.println("Bitte geben Sie die Mengeneinheit ein.");
        mge = in.readLine();
        System.out.println("Bitte geben Sie den Preis pro Mengeneinheit ein.");
        preis = Double.parseDouble(in.readLine());
        do{
            System.out.println("Bitte geben Sie den Steuersatz ein.");
            steu = Integer.parseInt(in.readLine());
            if(!(steu==7 || steu==19))
                System.out.println("Ungültiger Steuersatz!");
        }while(!(steu==7 || steu==19));
                    
        oneLine.append(artnr).append(";").append(artbez).append(";").append(mge).append(";").append(preis).append(";").append(steu).append(";\n");
        raf1.seek(pointerTopDAT);
        raf1.writeBytes(oneLine.toString());
        isam = new ISAM(artnr,pointerTopDAT);
        isamList.add(isam);                    
        oneLine.setLength(0);
        pointerTopDAT = raf1.getFilePointer();
        
        
        isam = isamList.get(isamList.size()-1);
        oneLine.append(isam.getArtnr()).append(",").append(isam.getOffset()).append(";\n");
        raf2.writeBytes(oneLine.toString());
        oneLine.setLength(0);
        
        System.out.println("Erfolgreich aufgenommen.\n\nWeiter mit Enter.");
        in.readLine();
    }

    public static void sequentiellerZugriff(RandomAccessFile raf1) throws IOException, ClassNotFoundException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.printf("%6s | %15s | %10s | %7s | %10s\n","ArtNr","Bezeichnung","Einheit","Preis","Steuersatz in %");
        raf1.seek(0);
        while(null!=(readline = raf1.readLine())){
            split = readline.split(";");
            System.out.printf("%6d | %15s | %10s | %6.2f€ | %6d \n",Integer.parseInt(split[0]),split[1],split[2],Double.parseDouble(split[3]),Integer.parseInt(split[4]));
        }
        System.out.println("\nWeiter mit Enter Taste.");
        in.read();
    }

    public static void lesenderDirekzugriff(RandomAccessFile raf1) throws IOException, ClassNotFoundException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        int temp = 0, search;
        System.out.println("ARTNR\tOffset"); //Testausgabe
        for(int i=0;i<isamList.size();i++){
            isam = (ISAM) isamList.get(i);
            isam.print(); //Testausgabe
        }
        System.out.println("Bitte Such-Nummer(Artikelnummer) eingeben.");
        search = Integer.parseInt(in.readLine());
        for(int i=0;i<isamList.size();i++){
            isam = (ISAM) isamList.get(i);
            if(search==isam.getArtnr()){
                raf1.seek(isam.getOffset());
                split = raf1.readLine().split(";");
                System.out.printf("%15s | %10s | %6s | %6s\n","Bezeichnung","Einheit","Preis","Steuerstatz in %");
                System.out.printf("%15s | %10s | %6.2f | %6d\n",split[1],split[2],Double.parseDouble(split[3]),Integer.parseInt(split[4]));
                System.out.println();
                temp++;
            
            }
        }
        if(temp==0){
            System.out.println("Artikelnummer nicht gefunden.");
        }
        //temp = 0;
        System.out.println("\nWeiter mit Enter.");
        in.read();
    }

    public static void beenden(RandomAccessFile raf1,RandomAccessFile raf2) throws IOException, ClassNotFoundException {
     
        raf2.setLength(0);
        
        for(int i=0;i<isamList.size();i++){
            isam = (ISAM) isamList.get(i);
            oneLine.append(isam.getArtnr()).append(",").append(isam.getOffset()).append(";\n");
            raf2.writeBytes(oneLine.toString());
            oneLine.setLength(0);
        }
        
        
        
        raf2.close();
        raf1.close();
    }
} //End Hauptprogramm


