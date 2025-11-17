/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import util.FileManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AgendaManager dengan Auto-Save dan Auto-Load
 * Data akan tersimpan otomatis ke file dan dimuat saat aplikasi dibuka
 * 
 * @author Dimas
 */
public class AgendaManager {
    private List<Agenda> agendaList;
    private static AgendaManager instance; // Singleton pattern
    
    // File path untuk auto-save
    private static final String AUTO_SAVE_FILE = "agenda_data.json";
    
    // Private constructor (Singleton)
    private AgendaManager() {
        agendaList = new ArrayList<>();
        // Auto-load data saat aplikasi dibuka
        loadDataFromFile();
    }
    
    // Get instance (Singleton pattern)
    public static AgendaManager getInstance() {
        if (instance == null) {
            instance = new AgendaManager();
        }
        return instance;
    }
    
    /**
     * Load data dari file saat aplikasi dibuka
     */
    private void loadDataFromFile() {
        try {
            List<Agenda> loadedData = FileManager.importFromJSON(AUTO_SAVE_FILE);
            if (loadedData != null && !loadedData.isEmpty()) {
                agendaList = loadedData;
                System.out.println("✓ Data berhasil dimuat: " + agendaList.size() + " agenda");
            }
        } catch (Exception e) {
            // File belum ada atau error, mulai dengan list kosong
            System.out.println("ℹ Memulai dengan data kosong (file belum ada)");
            agendaList = new ArrayList<>();
        }
    }
    
    /**
     * Simpan data ke file secara otomatis
     */
    private void saveDataToFile() {
        try {
            FileManager.exportToJSON(agendaList, AUTO_SAVE_FILE);
            System.out.println("✓ Data tersimpan otomatis");
        } catch (Exception e) {
            System.err.println("✗ Error saat menyimpan data: " + e.getMessage());
        }
    }
    
    // CRUD Operations dengan Auto-Save
    
    /**
     * Tambah agenda baru dan simpan otomatis
     */
    public void tambahAgenda(Agenda agenda) {
        agendaList.add(agenda);
        saveDataToFile(); // Auto-save setelah perubahan
    }
    
    /**
     * Update agenda dan simpan otomatis
     */
    public void updateAgenda(String id, Agenda agendaBaru) {
        for (int i = 0; i < agendaList.size(); i++) {
            if (agendaList.get(i).getId().equals(id)) {
                agendaBaru.setId(id);
                agendaList.set(i, agendaBaru);
                saveDataToFile(); // Auto-save setelah perubahan
                break;
            }
        }
    }
    
    /**
     * Hapus agenda dan simpan otomatis
     */
    public void hapusAgenda(String id) {
        agendaList.removeIf(agenda -> agenda.getId().equals(id));
        saveDataToFile(); // Auto-save setelah perubahan
    }
    
    /**
     * Get semua agenda
     */
    public List<Agenda> getAllAgenda() {
        return new ArrayList<>(agendaList);
    }
    
    /**
     * Get agenda berdasarkan ID
     */
    public Agenda getAgendaById(String id) {
        return agendaList.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    // Filter methods
    
    /**
     * Filter agenda berdasarkan kategori
     */
    public List<Agenda> filterByKategori(String kategori) {
        return agendaList.stream()
                .filter(a -> a.getKategori().equals(kategori))
                .collect(Collectors.toList());
    }
    
    /**
     * Filter agenda berdasarkan status selesai/belum
     */
    public List<Agenda> filterByStatus(boolean selesai) {
        return agendaList.stream()
                .filter(a -> a.isSelesai() == selesai)
                .collect(Collectors.toList());
    }
    
    /**
     * Search agenda berdasarkan keyword
     */
    public List<Agenda> searchAgenda(String keyword) {
        return agendaList.stream()
                .filter(a -> a.getJudul().toLowerCase().contains(keyword.toLowerCase()) ||
                            a.getDeskripsi().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    /**
     * Set agenda list (untuk import) dan simpan
     */
    public void setAgendaList(List<Agenda> list) {
        this.agendaList = list;
        saveDataToFile(); // Auto-save setelah import
    }
}
