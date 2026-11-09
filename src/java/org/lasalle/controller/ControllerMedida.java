/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.lasalle.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.lasalle.model.Medida;

/**
 *
 * @author Halo
 */
public class ControllerMedida {
    public List<Medida> getMedidas() throws SQLException{
        List<Medida> lista = new ArrayList<>();
        
        //declaramos el query a ejecutar
        String sql = "SELECT * FROM medida";
        ConnectionMysql connMysql = new ConnectionMysql();
        try (Connection conn = connMysql.open();
             PreparedStatement pstm = conn.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            while(rs.next()){
                lista.add(fillMedida(rs));
            }
        }
        return lista;
    }
    
    public Medida getMedida(int idMedida) throws SQLException{
        //declaramos el query a ejecutar
        String sql = "SELECT * FROM medida WHERE idMedida = ?";
        ConnectionMysql connMysql = new ConnectionMysql();
        try (Connection conn = connMysql.open();
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, idMedida);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return fillMedida(rs);
                }
            }
        }
        return null;
    }
    
    public boolean insertMedida(Medida medida) throws SQLException{
        //declaramos el query a ejecutar
        String sql = "INSERT INTO medida (idUsuario, glucosa, fechaHora, contexto, notas) VALUES (?, ?, ?, ?, ?)";
        ConnectionMysql connMysql = new ConnectionMysql();
        try (Connection conn = connMysql.open();
             PreparedStatement pstm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstm.setInt(1, medida.getIdUsuario());
            pstm.setInt(2, medida.getGlucosa());
            pstm.setString(3, medida.getFechaHora());
            pstm.setString(4, medida.getContexto());
            pstm.setString(5, medida.getNotas());
            
            if (pstm.executeUpdate() == 0) {
                return false;
            }
            
            try (ResultSet rs = pstm.getGeneratedKeys()) {
                if (rs.next()) {
                    medida.setIdMedida(rs.getInt(1));
                }
            }
            return true;
        }
    }
    
    public Medida fillMedida(ResultSet rs) throws SQLException{
        Medida medida = new Medida();
        medida.setIdMedida(rs.getInt("idMedida"));
        medida.setIdUsuario(rs.getInt("idUsuario"));
        medida.setGlucosa(rs.getInt("glucosa"));
        medida.setFechaHora(rs.getString("fechaHora"));
        medida.setContexto(rs.getString("contexto"));
        medida.setNotas(rs.getString("notas"));
        return medida;
    }
}
