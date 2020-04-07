package hu.szacsuri.norbert;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
//----------------- fájlkezelés ---------------------------
        if (args.length != 2) {
            System.out.println("Nem megfelelő mennyiségű paraméter!");
            System.out.println("Használat: program input_file_path output_file_path");
            System.out.println("");
            System.out.println("Például: program \"./../Examples/in.1.txt\" \"./../Examples/out.1.txt\"");
            System.exit(-1);
        }

        File inputFile = new File(args[0]);
        if (!inputFile.exists() || !inputFile.canRead()) {
            System.out.println("Nem létező, vagy nem olvasható fájl: " + inputFile.getAbsolutePath());
            System.exit(-2);
        }

        File outputFile = new File(args[1]);
        if (!outputFile.exists()) {
            try {
                outputFile.getParentFile().mkdirs();
                outputFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Nem hozható létre a fájl: " + outputFile.getAbsolutePath());
                System.exit(-3);
            }
        }

        if (!outputFile.canWrite()) {
            System.out.println("Nem írható a fájl: " + outputFile.getAbsolutePath());
            System.exit(-4);
        }

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(outputFile);
        } catch (FileNotFoundException e) {
            System.out.println("Fájl nem található: " + outputFile.getAbsolutePath());
            System.exit(-5);
        }

//----------------- számítások ---------------------------

        // kszi \ eta egyuttes eloszlás értékei (diszkrét)
        // kszi az oszlop, eta a sor
        double[][] kszi_eta_eloszlas = loadMatrixFromFile(inputFile);
        int iMax = kszi_eta_eloszlas.length;            // i == row index
        int jMax = iMax > 0                             // j == col index
                ? kszi_eta_eloszlas[0].length
                : 0;

        // együttes eloszlás entrópiája
        double kszi_eta_egyuttes_entropia = calcEntropyOfMatrix(kszi_eta_eloszlas);
        pw.println("Együttes eloszlás entrópiája: " + kszi_eta_egyuttes_entropia);

        // peremeloszlások
        double[] eta_peremeloszlas = new double[jMax]; //default value is 0
        double[] kszi_peremeloszlas = new double[iMax]; //default value is 0
        for (int i = 0; i < iMax; i++) {
            for (int j = 0; j < jMax; j++) {
                kszi_peremeloszlas[i] += kszi_eta_eloszlas[i][j];
                eta_peremeloszlas[j] += kszi_eta_eloszlas[i][j];
            }
        }

        // peremeloszlások entrópiája
        double kszi_peremeloszlas_entropia = calcEntropyOfArray(kszi_peremeloszlas);
        double eta_peremeloszlas_entropia = calcEntropyOfArray(eta_peremeloszlas);
        pw.println("Kszi peremeloszlas entrópiája: " + kszi_peremeloszlas_entropia);
        pw.println("Eta peremeloszlas entrópiája: " + eta_peremeloszlas_entropia);

        // feltételes entrópiák
        // H(kszi, eta)   = H(eta) + H(kszi | eta)   = H(kszi) + H(eta | kszi)
        double kszi_felteteles_eta_entropia = kszi_eta_egyuttes_entropia - eta_peremeloszlas_entropia;
        double eta_felteteles_kszi_entropia = kszi_eta_egyuttes_entropia - kszi_peremeloszlas_entropia;
        pw.println("Kszi feltételes entrópiája eta-ra nézve: " + kszi_felteteles_eta_entropia);
        pw.println("Eta feltételes entrópiája kszi-re nézve: " + eta_felteteles_kszi_entropia);


        // kölcsönös információ mennyiség
        // I(kszi, eta) = H(kszi) + H(eta) - H(kszi, eta)
        double kolcsonos_informacio_mennyiseg = kszi_peremeloszlas_entropia + eta_peremeloszlas_entropia - kszi_eta_egyuttes_entropia;
        pw.println("Kszi és eta kölcsönös információ mennyisége: " + kolcsonos_informacio_mennyiseg);

        pw.close();
    }

    private static double[][] loadMatrixFromFile(File inputFile) {
        try (Stream<String> stream = Files.lines(inputFile.toPath())) {
            List<List<String>> stringMatrix = new ArrayList<>();
            stream.forEachOrdered((line) -> {
                List<String> stringRow = Arrays.asList(line.split(" "));
                stringMatrix.add(stringRow);
            });

            int nRows = stringMatrix.size();
            int nCols = nRows > 0 ? stringMatrix.get(0).size() : 0;
            double[][] matrix = new double[nRows][nCols];
            for (int i = 0; i < stringMatrix.size(); i++) {
                List<String> stringRow = stringMatrix.get(i);
                for (int j = 0; j < stringRow.size(); j++) {
                    matrix[i][j] = Double.parseDouble(stringRow.get(j));
                }
            }

            return matrix;
        } catch (IOException e) {
            System.out.println("Nem lehet megnyitni a fájlt: " + inputFile.getAbsolutePath());
            System.exit(-6);
        }

        return null;
    }

    // H(kszi, eta) = sum(sum( -1 * (P_ij * log2(P_ij)) ))
    private static double calcEntropyOfMatrix(double[][] matrix) {
        double entrophy = 0;

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                entrophy += calcEntropyOfScalar(matrix[i][j]);
            }
        }

        return entrophy;
    }

    // H(kszi) = sum( -1 * (P_i * log2(P_i)) )
    private static double calcEntropyOfArray(double[] array) {
        double entrophy = 0;

        for (int i = 0; i < array.length; i++) {
            entrophy += calcEntropyOfScalar(array[i]);
        }

        return entrophy;
    }

    // H(x) = -1 * (x * log2(x))
    private static double calcEntropyOfScalar(double x) {
        if (x != 0) {
            return -1 * (x * log2(x));
        } else {
            return 0;
        }
    }

    private static double log2(double x) {
        return (Math.log(x) / Math.log(2));
    }
}
