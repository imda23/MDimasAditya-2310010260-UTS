/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 *
 * @author Dimas
 */
public class Agenda implements Serializable {
    // Attributes (Encapsulation - private)
    private String id;
    private String judul;
    private String deskripsi;
    private LocalDate tanggal;
    private String kategori;
    private String prioritas;
    private boolean selesai;
    
    // Constructor
    public Agenda() {
        this.id = UUID.randomUUID().toString();
        this.selesai = false;
    }
    
    public Agenda(String judul, String deskripsi, LocalDate tanggal, 
                  String kategori, String prioritas) {
        this();
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.tanggal = tanggal;
        this.kategori = kategori;
        this.prioritas = prioritas;
    }
    
    // Getter and Setter (Encapsulation)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }
    
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    
    public LocalDate getTanggal() { return tanggal; }
    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }
    
    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    
    public String getPrioritas() { return prioritas; }
    public void setPrioritas(String prioritas) { this.prioritas = prioritas; }
    
    public boolean isSelesai() { return selesai; }
    public void setSelesai(boolean selesai) { this.selesai = selesai; }
    
    // Method untuk mendapatkan status
    public String getStatus() {
        return selesai ? "Selesai" : "Belum Selesai";
    }
    
    @Override
    public String toString() {
        return judul + " - " + tanggal + " [" + prioritas + "]";
    }
}
