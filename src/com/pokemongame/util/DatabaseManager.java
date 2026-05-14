/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author user
 */
public class DatabaseManager {
    // Pastikan nama database sama dengan yang kamu buat di phpMyAdmin
    private static final String URL = "jdbc:mysql://localhost:3306/pokemon_game_db";
    private static final String USER = "root"; // Default XAMPP
    private static final String PASS = "";     // Default XAMPP biasanya kosong

    public static Connection getConnection() {
        try {
            // Memastikan driver JDBC termuat
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL tidak ditemukan! Pastikan file JAR sudah di-add ke Libraries.");
            return null;
        } catch (SQLException e) {
            System.err.println("Gagal konek ke Database! Pastikan XAMPP/MySQL aktif.");
            e.printStackTrace();
            return null;
        }
    }
}