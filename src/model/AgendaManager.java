/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Dimas
 */
public class AgendaManager {
    private List<Agenda> agendaList;
    private static AgendaManager instance; // Singleton pattern
    
    // Private constructor (Singleton)
    private AgendaManager() {
        agendaList = new ArrayList<>();
    }
    
    // Get instance (Singleton pattern)
    public static AgendaManager getInstance() {
        if (instance == null) {
            instance = new AgendaManager();
        }
        return instance;
    }
    
    // CRUD Operations
    public void tambahAgenda(Agenda agenda) {
        agendaList.add(agenda);
    }
    
    public void updateAgenda(String id, Agenda agendaBaru) {
        for (int i = 0; i < agendaList.size(); i++) {
            if (agendaList.get(i).getId().equals(id)) {
                agendaBaru.setId(id);
                agendaList.set(i, agendaBaru);
                break;
            }
        }
    }
    
    public void hapusAgenda(String id) {
        agendaList.removeIf(agenda -> agenda.getId().equals(id));
    }
    
    public List<Agenda> getAllAgenda() {
        return new ArrayList<>(agendaList);
    }
    
    public Agenda getAgendaById(String id) {
        return agendaList.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    // Filter methods
    public List<Agenda> filterByKategori(String kategori) {
        return agendaList.stream()
                .filter(a -> a.getKategori().equals(kategori))
                .collect(Collectors.toList());
    }
    
    public List<Agenda> filterByStatus(boolean selesai) {
        return agendaList.stream()
                .filter(a -> a.isSelesai() == selesai)
                .collect(Collectors.toList());
    }
    
    public List<Agenda> searchAgenda(String keyword) {
        return agendaList.stream()
                .filter(a -> a.getJudul().toLowerCase().contains(keyword.toLowerCase()) ||
                            a.getDeskripsi().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    public void setAgendaList(List<Agenda> list) {
        this.agendaList = list;
    }
}
