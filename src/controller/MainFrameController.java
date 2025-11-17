/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.Agenda;
import model.AgendaManager;
import util.FileManager;
import view.MainFrame;
import view.TambahAgendaDialog;
import view.EditAgendaDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.io.File;
import java.util.List;

/**
 * Controller untuk MainFrame - Menghubungkan View dengan Model
 * Menggunakan pattern MVC (Model-View-Controller)
 * @author Dimas
 */
public class MainFrameController {
    // Referensi ke View dan Model
    private MainFrame view;
    private AgendaManager manager;
    private DefaultTableModel tableModel;
    
    /**
     * Constructor - Inisialisasi controller
     * @param view MainFrame yang akan dikontrol
     */
    public MainFrameController(MainFrame view) {
        this.view = view;
        this.manager = AgendaManager.getInstance();
        this.tableModel = (DefaultTableModel) view.getTabelAgenda().getModel();
        
        // Setup komponen dan listeners
        initializeComponents();
        attachEventListeners();
        
        // Load data awal dan update dashboard
        loadDataToTable(manager.getAllAgenda());
        updateDashboard();
    }
    
    /**
     * Inisialisasi komponen UI
     */
    private void initializeComponents() {
        // Set window di tengah layar
        view.setLocationRelativeTo(null);
        
        // Setup table
        setupTable();
        
        // Setup keyboard shortcuts
        setupKeyboardShortcuts();
    }
    
    /**
     * Setup properti table
     */
    private void setupTable() {
        JTable table = view.getTabelAgenda();
        
        // Sembunyikan kolom ID (tetap ada tapi tidak terlihat)
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        
        // Set lebar kolom
        table.getColumnModel().getColumn(1).setPreferredWidth(250); // Judul
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Tanggal
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Kategori
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Prioritas
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // Status
    }
    
    /**
     * Setup keyboard shortcuts
     */
    private void setupKeyboardShortcuts() {
        // Ctrl+N untuk tambah agenda
        KeyStroke ctrlN = KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK);
        view.getRootPane().registerKeyboardAction(
            e -> handleTambahAgenda(),
            ctrlN,
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        // Delete key untuk hapus agenda
        KeyStroke delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        view.getRootPane().registerKeyboardAction(
            e -> handleHapusAgenda(),
            delete,
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        // Enter key untuk edit (saat ada baris terpilih)
        view.getTabelAgenda().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    handleEditAgenda();
                }
            }
        });
    }
    
    /**
     * Attach semua event listeners ke komponen UI
     */
    private void attachEventListeners() {
        // Tombol CRUD
        view.getBtnTambah().addActionListener(e -> handleTambahAgenda());
        view.getBtnEdit().addActionListener(e -> handleEditAgenda());
        view.getBtnHapus().addActionListener(e -> handleHapusAgenda());
        
        // Tombol Import/Export
        view.getBtnImport().addActionListener(e -> handleImport());
        view.getBtnExport().addActionListener(e -> handleExport());
        
        // Tombol Search & Filter
        view.getBtnCari().addActionListener(e -> handleSearch());
        view.getBtnRefresh().addActionListener(e -> handleRefresh());
        
        // ComboBox Filter
        view.getCmbKategori().addActionListener(e -> applyFilters());
        view.getCmbStatus().addActionListener(e -> applyFilters());
        
        // Search field - trigger search saat Enter
        view.getTxtSearch().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleSearch();
                }
            }
        });
        
        // Double click pada table untuk edit
        view.getTabelAgenda().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleEditAgenda();
                }
            }
        });
    }
    
    /**
     * Handler untuk tombol Tambah Agenda
     */
    private void handleTambahAgenda() {
        TambahAgendaDialog dialog = new TambahAgendaDialog(view, true);
        TambahAgendaController controller = new TambahAgendaController(dialog, this);
        dialog.setVisible(true);
    }
    
    /**
     * Handler untuk tombol Edit Agenda
     */
    private void handleEditAgenda() {
        int selectedRow = view.getTabelAgenda().getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view,
                "Silakan pilih agenda yang akan diedit!",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Ambil ID agenda dari kolom tersembunyi
        String agendaId = (String) tableModel.getValueAt(selectedRow, 0);
        Agenda agenda = manager.getAgendaById(agendaId);
        
        if (agenda != null) {
            EditAgendaDialog dialog = new EditAgendaDialog(view, true);
            EditAgendaController controller = new EditAgendaController(dialog, this, agenda);
            dialog.setVisible(true);
        }
    }
    
    /**
     * Handler untuk tombol Hapus Agenda
     */
    private void handleHapusAgenda() {
        int selectedRow = view.getTabelAgenda().getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view,
                "Silakan pilih agenda yang akan dihapus!",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Konfirmasi penghapusan
        String judul = (String) tableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(view,
            "Apakah Anda yakin ingin menghapus agenda:\n\"" + judul + "\"?",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String agendaId = (String) tableModel.getValueAt(selectedRow, 0);
            manager.hapusAgenda(agendaId);
            
            // Refresh tampilan
            refreshData();
            
            JOptionPane.showMessageDialog(view,
                "Agenda berhasil dihapus!",
                "Sukses",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Handler untuk tombol Import
     */
    private void handleImport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Data Agenda");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "JSON files", "json"));
        
        int result = fileChooser.showOpenDialog(view);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                List<Agenda> importedData = FileManager.importFromJSON(file.getAbsolutePath());
                
                // Konfirmasi: Replace atau Append
                String[] options = {"Replace (Ganti Semua)", "Append (Tambahkan)", "Batal"};
                int choice = JOptionPane.showOptionDialog(view,
                    "Ditemukan " + importedData.size() + " agenda.\nPilih mode import:",
                    "Mode Import",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[1]);
                
                if (choice == 0) { // Replace
                    manager.setAgendaList(importedData);
                } else if (choice == 1) { // Append
                    for (Agenda agenda : importedData) {
                        manager.tambahAgenda(agenda);
                    }
                } else {
                    return; // Cancel
                }
                
                refreshData();
                
                JOptionPane.showMessageDialog(view,
                    "Data berhasil diimport!\nTotal: " + importedData.size() + " agenda",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view,
                    "Error saat import data:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Handler untuk tombol Export
     */
    private void handleExport() {
        String[] options = {"JSON", "TXT", "Batal"};
        int choice = JOptionPane.showOptionDialog(view,
            "Pilih format export:",
            "Export Data",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        if (choice == 2 || choice == JOptionPane.CLOSED_OPTION) {
            return; // Cancel
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Data Agenda");
        
        if (choice == 0) { // JSON
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "JSON files", "json"));
            fileChooser.setSelectedFile(new File("agenda_export.json"));
        } else { // TXT
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Text files", "txt"));
            fileChooser.setSelectedFile(new File("agenda_export.txt"));
        }
        
        int result = fileChooser.showSaveDialog(view);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                List<Agenda> allAgenda = manager.getAllAgenda();
                
                if (choice == 0) { // JSON
                    FileManager.exportToJSON(allAgenda, file.getAbsolutePath());
                } else { // TXT
                    FileManager.exportToTXT(allAgenda, file.getAbsolutePath());
                }
                
                JOptionPane.showMessageDialog(view,
                    "Data berhasil diexport ke:\n" + file.getAbsolutePath(),
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view,
                    "Error saat export data:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Handler untuk tombol Cari
     */
    private void handleSearch() {
        String keyword = view.getTxtSearch().getText().trim();
        
        if (keyword.isEmpty()) {
            handleRefresh();
            return;
        }
        
        List<Agenda> results = manager.searchAgenda(keyword);
        loadDataToTable(results);
        updateDashboard();
        
        // Tampilkan notifikasi hasil pencarian
        view.getTxtSearch().setToolTipText(
            "Ditemukan " + results.size() + " hasil untuk '" + keyword + "'");
    }
    
    /**
     * Handler untuk tombol Refresh
     */
    private void handleRefresh() {
        // Reset semua filter
        view.getTxtSearch().setText("");
        view.getCmbKategori().setSelectedIndex(0);
        view.getCmbStatus().setSelectedIndex(0);
        
        // Reload semua data
        refreshData();
    }
    
    /**
     * Apply filters berdasarkan combo box
     */
    private void applyFilters() {
        List<Agenda> filteredData = manager.getAllAgenda();
        
        // Filter kategori
        String kategori = (String) view.getCmbKategori().getSelectedItem();
        if (kategori != null && !kategori.equals("Semua")) {
            filteredData = manager.filterByKategori(kategori);
        }
        
        // Filter status
        String status = (String) view.getCmbStatus().getSelectedItem();
        if (status != null && !status.equals("Semua")) {
            boolean selesai = status.equals("Selesai");
            List<Agenda> temp = filteredData;
            filteredData = manager.filterByStatus(selesai);
            
            // Intersect dengan hasil filter sebelumnya
            if (!kategori.equals("Semua")) {
                filteredData.retainAll(temp);
            }
        }
        
        loadDataToTable(filteredData);
        updateDashboard();
    }
    
    /**
     * Load data agenda ke table
     * @param agendaList List agenda yang akan ditampilkan
     */
    public void loadDataToTable(List<Agenda> agendaList) {
        // Clear table
        tableModel.setRowCount(0);
        
        // Load data
        for (Agenda agenda : agendaList) {
            Object[] row = {
                agenda.getId(),           // Hidden column
                agenda.getJudul(),
                agenda.getTanggal(),
                agenda.getKategori(),
                agenda.getPrioritas(),
                agenda.getStatus()
            };
            tableModel.addRow(row);
        }
    }
    
    /**
     * Refresh semua data dari manager
     */
    public void refreshData() {
        loadDataToTable(manager.getAllAgenda());
        updateDashboard();
    }
    
    /**
     * Update dashboard cards dengan statistik terbaru
     */
    public void updateDashboard() {
        List<Agenda> allAgenda = manager.getAllAgenda();
        
        // Total agenda
        int total = allAgenda.size();
        view.getLblTotalValue().setText(String.valueOf(total));
        
        // Agenda selesai
        long selesai = allAgenda.stream()
            .filter(Agenda::isSelesai)
            .count();
        view.getLblSelesaiValue().setText(String.valueOf(selesai));
        
        // Agenda belum selesai
        int belum = total - (int)selesai;
        view.getLblBelumValue().setText(String.valueOf(belum));
        
        // Prioritas tinggi
        long prioritasTinggi = allAgenda.stream()
            .filter(a -> a.getPrioritas().equals("Tinggi"))
            .count();
        view.getLblPrioritasValue().setText(String.valueOf(prioritasTinggi));
    }
}
