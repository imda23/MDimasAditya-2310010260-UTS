/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.Agenda;
import model.AgendaManager;
import view.EditAgendaDialog;

import javax.swing.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Controller untuk EditAgendaDialog
 * Menangani logic edit agenda yang sudah ada
 *
 * @author Dimas
 */
public class EditAgendaController {
    private EditAgendaDialog view;
    private MainFrameController mainController;
    private AgendaManager manager;
    private Agenda currentAgenda;
    
    /**
     * Constructor
     * @param view Dialog edit agenda
     * @param mainController Controller main frame
     * @param agenda Agenda yang akan diedit
     */
    public EditAgendaController(EditAgendaDialog view, 
                               MainFrameController mainController,
                               Agenda agenda) {
        this.view = view;
        this.mainController = mainController;
        this.manager = AgendaManager.getInstance();
        this.currentAgenda = agenda;
        
        initializeComponents();
        loadAgendaData();
        attachEventListeners();
    }
    
    /**
     * Inisialisasi komponen dialog
     */
    private void initializeComponents() {
        // Set posisi dialog di tengah parent
        view.setLocationRelativeTo(view.getParent());
        
        // Format spinner tanggal
        JSpinner.DateEditor editor = new JSpinner.DateEditor(
            view.getSpinnerTanggal(), "dd/MM/yyyy");
        view.getSpinnerTanggal().setEditor(editor);
        
        // Sembunyikan tombol Simpan, tampilkan tombol Update
        view.getBtnSimpan().setVisible(false);
        view.getBtnUpdate().setVisible(true);
    }
    
    /**
     * Load data agenda ke form
     */
    private void loadAgendaData() {
        // Set judul
        view.getTxtJudul().setText(currentAgenda.getJudul());
        
        // Set deskripsi
        view.getTxtDeskripsi().setText(currentAgenda.getDeskripsi());
        
        // Set tanggal
        Date date = Date.from(currentAgenda.getTanggal()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant());
        view.getSpinnerTanggal().setValue(date);
        
        // Set kategori
        view.getCmbKategori().setSelectedItem(currentAgenda.getKategori());
        
        // Set prioritas
        view.getCmbPrioritas().setSelectedItem(currentAgenda.getPrioritas());
        
        // Set status
        view.getChkSelesai().setSelected(currentAgenda.isSelesai());
        
        // Focus ke field judul
        view.getTxtJudul().requestFocus();
        view.getTxtJudul().selectAll();
    }
    
    /**
     * Attach event listeners
     */
    private void attachEventListeners() {
        // Tombol Update
        view.getBtnUpdate().addActionListener(e -> handleUpdate());
        
        // Tombol Batal
        view.getBtnBatal().addActionListener(e -> view.dispose());
        
        // Checkbox selesai - konfirmasi jika dicentang
        view.getChkSelesai().addActionListener(e -> {
            if (view.getChkSelesai().isSelected()) {
                int confirm = JOptionPane.showConfirmDialog(view,
                    "Tandai agenda ini sebagai selesai?",
                    "Konfirmasi",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                
                if (confirm != JOptionPane.YES_OPTION) {
                    view.getChkSelesai().setSelected(false);
                }
            }
        });
    }
    
    /**
     * Handler untuk tombol Update
     */
    private void handleUpdate() {
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
            boolean selesai = view.getChkSelesai().isSelected();
            
            // Buat objek Agenda baru dengan data updated
            Agenda updatedAgenda = new Agenda(judul, deskripsi, tanggal, kategori, prioritas);
            updatedAgenda.setSelesai(selesai);
            
            // Update di manager
            manager.updateAgenda(currentAgenda.getId(), updatedAgenda);
            
            // Update main frame
            mainController.refreshData();
            
            // Tampilkan konfirmasi
            JOptionPane.showMessageDialog(view,
                "Agenda berhasil diupdate!",
                "Sukses",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Tutup dialog
            view.dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                "Error saat mengupdate agenda:\n" + e.getMessage(),
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
