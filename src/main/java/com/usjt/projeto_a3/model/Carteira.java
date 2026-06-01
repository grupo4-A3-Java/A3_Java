package com.usjt.projeto_a3.model;

import java.time.LocalDateTime;

public class Carteira {
    
    private int id;
    private int usuarioId;
    private int ativoId;
    private double quantidadeTotal;
    private double precoMedio;
    private LocalDateTime ultimaAtualizacao;

    public Carteira() {
    }

    public Carteira(int id, int usuarioId, int ativoId, double quantidadeTotal, double precoMedio, LocalDateTime ultimaAtualizacao) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.ativoId = ativoId;
        this.quantidadeTotal = quantidadeTotal;
        this.precoMedio = precoMedio;
        this.ultimaAtualizacao = ultimaAtualizacao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public int getAtivoId() {
        return ativoId;
    }

    public void setAtivoId(int ativoId) {
        this.ativoId = ativoId;
    }

    public double getQuantidadeTotal() {
        return quantidadeTotal;
    }

    public void setQuantidadeTotal(double quantidadeTotal) {
        this.quantidadeTotal = quantidadeTotal;
    }

    public double getPrecoMedio() {
        return precoMedio;
    }

    public void setPrecoMedio(double precoMedio) {
        this.precoMedio = precoMedio;
    }

    public LocalDateTime getUltimaAtualizacao() {
        return ultimaAtualizacao;
    }

    public void setUltimaAtualizacao(LocalDateTime ultimaAtualizacao) {
        this.ultimaAtualizacao = ultimaAtualizacao;
    }
}