/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import model.Agenda;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Dimas
 */
public class FileManager {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .setPrettyPrinting()
            .create();
    
    // Export ke JSON
    public static void exportToJSON(List<Agenda> agendaList, String filepath) throws IOException {
        try (Writer writer = new FileWriter(filepath)) {
            gson.toJson(agendaList, writer);
        }
    }
    
    // Import dari JSON
    public static List<Agenda> importFromJSON(String filepath) throws IOException {
        try (Reader reader = new FileReader(filepath)) {
            return gson.fromJson(reader, new TypeToken<List<Agenda>>(){}.getType());
        }
    }
    
    // Export ke TXT
    public static void exportToTXT(List<Agenda> agendaList, String filepath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            writer.write("=== DAFTAR AGENDA PRIBADI ===\n\n");
            for (Agenda agenda : agendaList) {
                writer.write("Judul: " + agenda.getJudul() + "\n");
                writer.write("Deskripsi: " + agenda.getDeskripsi() + "\n");
                writer.write("Tanggal: " + agenda.getTanggal() + "\n");
                writer.write("Kategori: " + agenda.getKategori() + "\n");
                writer.write("Prioritas: " + agenda.getPrioritas() + "\n");
                writer.write("Status: " + agenda.getStatus() + "\n");
                writer.write("-----------------------------------\n\n");
            }
        }
    }
}
