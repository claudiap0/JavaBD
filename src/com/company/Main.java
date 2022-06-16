package com.company;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private Connection c;

    public Main() {

    }

    //scriere in tabela
    public void creareTabela(String numeFisier) throws Exception {
        c = DriverManager.getConnection("jdbc:sqlite:intretinere.db");
        System.out.println("Conexiune creata");
        try(Statement s = c.createStatement()) {
            try (ResultSet r = c.getMetaData().getTables(null, null, "Intretinere", new String[]{"TABLE"})) {
                if (!r.next()) {
                    String cmd = "create table Intretinere (" +
                            "Numar_apartamente integer," +
                            "Nume varchar(30)," +
                            "Suprafata integer," +
                            "Numar persoane integer)";
                    s.executeUpdate(cmd);

                } else {
                    s.executeUpdate("delete from Intretinere");
                }
            }
            //adaugare inregistrari
            try (BufferedReader in = new BufferedReader(new FileReader(numeFisier))) {
                in.lines().forEach(linie -> {
                    StringBuilder sb = new StringBuilder("insert into Intretinere values(");
                    String[] t = linie.split(",");
                    sb.append(t[0].trim()).append(",");
                    sb.append("'").append(t[1]).append("', ");
                    sb.append(t[2].trim()).append(",");
                    sb.append("'").append(t[3]).append("', ");
                    sb.append("'").append(t[4]).append("', ");
                    sb.append("'").append(t[5]).append("')");
                    System.out.println(sb);
                    try {
                        s.executeUpdate(sb.toString());
                    } catch (Exception ex) {
                        System.err.println(ex);
                    }
                });
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    //citire din bd
    public void printare() {
        try(Statement s = c.createStatement()) {
            try(ResultSet r = s.executeQuery("select * from Intretinere")) {
                while(r.next()) {
                    System.out.println(r.getInt(1)+", "+r.getString(2) +", "+r.getInt(3)+", "+r.getInt(4));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int printareSuprafatatotala() {
        int suma = 0;

        try(Statement s = c.createStatement()) {
            try(ResultSet r = s.executeQuery("select * from Intretinere")) {
                while(r.next()) {
                    System.out.println(r.getInt(1)+", "+r.getString(2) +", "+r.getInt(3)+", "+r.getInt(4));
                    suma += r.getInt(3);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suma;
    }
    public static void main(String[] args) {
        List<Factura> listaFacturi = new ArrayList<>();

        String fisier = "/Users/claudiapistol/IdeaProjects/subiectIntretinere/intretinere_facturi.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(fisier))) {
            String linie;
            while ((linie = reader.readLine()) != null) {
                String[] elementeLinie = linie.split(",");
                String denumire = elementeLinie[0];
                String repartizare = elementeLinie[1];
                double valoare = Double.parseDouble(elementeLinie[2]);

                Factura factura = new Factura(denumire, repartizare, valoare);
                listaFacturi.add(factura);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ;


//        for(Factura f: listaFacturi){
//            System.out.println(f);
//        }
//
//        //Exercitiul 1
//        for(Factura f: listaFacturi) {
//            Long numarFacturi = listaFacturi
//                    .stream()
//                    .filter(s -> s.getRepartizare() == f.getRepartizare())
//                    .collect(Collectors.counting());
//
//        }
        int nrFacturiSuprafata = 0, nrFacturiPersoane = 0;
        for (Factura f : listaFacturi) {
            if (f.getRepartizare().equals("suprafata")) {
                nrFacturiSuprafata += 1;
            } else if (f.getRepartizare().equals("persoane")) {
                nrFacturiPersoane += 1;
            }
        }

        System.out.println("Exercitiul 1");
        System.out.println("Numar facturi suprafata: " + nrFacturiSuprafata);
        System.out.println("Numar facturi persoane: " + nrFacturiPersoane);
        System.out.println();

        List<Double> rezultate = new ArrayList<>();
        List<String> rezultateNume= new ArrayList<>();
        for (Factura f : listaFacturi) {
            Double sumaTotalaFacturi = listaFacturi
                    .stream()
                    .filter(s -> s.getRepartizare().equals(f.getRepartizare()))
                    .collect(Collectors.summingDouble(Factura::getValoare));


            rezultate.add(sumaTotalaFacturi);
            rezultateNume.add(f.getRepartizare());

        }

        Set<Double> set = new HashSet<>(rezultate);
        rezultate.clear();
        rezultate.addAll(set);

        Set<String> set1 = new HashSet<>(rezultateNume);
        rezultateNume.clear();
        rezultateNume.addAll(set1);

        System.out.println("Exercitiul 2");
        System.out.println("Valoarea totala a facturilor pe tip de repartizare: ");
        for(int i=0;i< rezultate.size();i++) {
            System.out.println(rezultateNume.get(i) +" - " +rezultate.get(i));
        }

        Main main = new Main();
        int suma = main.printareSuprafatatotala();
        System.out.println(suma);
    }

}
