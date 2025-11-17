/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.Agenda;
import model.AgendaManager;
import view.TambahAgendaDialog;

import javax.swing.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Controller untuk TambahAgendaDialog
 * Menangani logic penambahan agenda baru
 *
 * @author Dimas
 */
public class TambahAgendaController {
    private TambahAgendaDialog view;
    private MainFrameController mainController;
    private AgendaManager manager;
    
    /**
     * Constructor
     * @param view Dialog tambah agenda
     * @param mainController Controller main frame untuk update data
     */
    public TambahAgendaController(TambahAgendaDialog view, MainFrameController mainController) {
        this.view = view;
        this.mainController = mainController;
        this.manager = AgendaManager.getInstance();
        
        initializeComponents();
        attachEventListeners();
    }
    
    /**
     * Inisialisasi komponen dialog
     */
    private void initializeComponents() {
        // Set posisi dialog di tengah parent
        view.setLocationRelativeTo(view.getParent());
        
        // Set default tanggal ke hari ini
        view.getSpinnerTanggal().setValue(new Date());
        
        // Format spinner tanggal
        JSpinner.DateEditor editor = new JSpinner.DateEditor(
            view.getSpinnerTanggal(), "dd/MM/yyyy");
        view.getSpinnerTanggal().setEditor(editor);
        
        // Focus ke field judul
        view.getTxtJudul().requestFocus();
    }
    
    /**
     * Attach event listeners
     */
    private void attachEventListeners() {
        // Tombol Simpan
        view.getBtnSimpan().addActionListener(e -> handleSimpan());
        
        // Tombol Batal
        view.getBtnBatal().addActionListener(e -> view.dispose());
        
        // Enter key pada text field untuk pindah focus
        view.getTxtJudul().addActionListener(e -> view.getTxtDeskripsi().requestFocus());
    }
    
    /**
     * Handler untuk tombol Simpan
     */
    private void handleSimpan() {
        // Validasi input
        if (!validateInput()) {
            return;
        }
        
        try {
            // Ambil data dari form
            String judul = view.getTxtJudul().getText().trim();
            String deskripsi = view.getTxtDeskripsi().getText().trim();
            
            // Konversi Date ke LocalDate
            Date dateValue = (Date) view.getSpinnerTanggal().getValue();
            LocalDate tanggal = dateValue.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
            
            String kategori = (String) view.getCmbKategori().getSelectedItem();
            String prioritas = (String) view.getCmbPrioritas().getSelectedItem();
            
            // Buat objek Agenda baru
            Agenda agenda = new Agenda(judul, deskripsi, tanggal, kategori, prioritas);
            
            // Simpan ke manager
            manager.tambahAgenda(agenda);
            
            // Update main frame
            mainController.refreshData();
            
            // Tampilkan konfirmasi
            JOptionPane.showMessageDialog(view,
                "Agenda berhasil ditambahkan!",
                "Sukses",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Tutup dialog
            view.dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                "Error saat menyimpan agenda:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Validasi input form
     * @return true jika valid, false jika tidak
     */
    private boolean validateInput() {
        // Validasi judul (wajib diisi)
        if (view.getTxtJudul().getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(view,
                "Judul agenda tidak boleh kosong!",
                "Validasi Error",
                JOptionPane.WARNING_MESSAGE);
            view.getTxtJudul().requestFocus();
            return false;
        }
        
        // Validasi panjang judul
        if (view.getTxtJudul().getText().trim().length() < 3) {
            JOptionPane.showMessageDialog(view,
                "Judul agenda minimal 3 karakter!",
                "Validasi Error",
                JOptionPane.WARNING_MESSAGE);
            view.getTxtJudul().requestFocus();
            return false;
        }
        
        // Validasi deskripsi (opsional, tapi jika diisi minimal 5 karakter)
        String deskripsi = view.getTxtDeskripsi().getText().trim();
        if (!deskripsi.isEmpty() && deskripsi.length() < 5) {
            JOptionPane.showMessageDialog(view,
                "Deskripsi minimal 5 karakter jika diisi!",
                "Validasi Error",
                JOptionPane.WARNING_MESSAGE);
            view.getTxtDeskripsi().requestFocus();
            return false;
        }
        
        // Validasi kategori
        if (view.getCmbKategori().getSelectedItem() == null) {
            JOptionPane.showMessageDialog(view,
                "Silakan pilih kategori!",
                "Validasi Error",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Validasi prioritas
        if (view.getCmbPrioritas().getSelectedItem() == null) {
            JOptionPane.showMessageDialog(view,
                "Silakan pilih prioritas!",
                "Validasi Error",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
}
