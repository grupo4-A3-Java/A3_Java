
package com.usjt.projeto_a3.model;
import java.time.LocalDateTime;

public class Operacao {
    private int id;
    private int usuarioId;
    private int ativoId;
    private String tipo; // Compra Ou venda
    private double quantidade;
    private double precoUnitario;
    private LocalDateTime dataOperacao;
    
    public Operacao(){
        
    }

    public Operacao(int id, int usuarioId, int ativoId, String tipo, double quantidade, double precoUnitario, LocalDateTime dataOperacao) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.ativoId = ativoId;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.dataOperacao = dataOperacao;
    }

    public int getId() {
        return id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public int getAtivoId() {
        return ativoId;
    }

    public String getTipo() {
        return tipo;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public double getPrecoUnitario() {
        return precoUnitario;
    }

    public LocalDateTime getDataOperacao() {
        return dataOperacao;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void setAtivoId(int ativoId) {
        this.ativoId = ativoId;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public void setPrecoUnitario(double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public void setDataOperacao(LocalDateTime dataOperacao) {
        this.dataOperacao = dataOperacao;
    }
    
   
}
