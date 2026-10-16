package com.mycompany.monitoringrealisasi;

import view.ConsoleView;

public class MonitoringRealisasi {
    public static void main(String[] args) {
        System.out.println("Sistem memuat data...");
        
        // Memanggil dan menjalankan menu buatan Chandra (C)
        ConsoleView view = new ConsoleView();
        view.mulai();
    }
}