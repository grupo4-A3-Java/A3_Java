
package com.usjt.projeto_a3.model;

public class Ativo {
    private int id;
    private String ticker;
    private String nome;
    private String tipo;
    
    public Ativo(){
        
    }

    public Ativo(int id, String ticker, String nome, String tipo) {
        this.id = id;
        this.ticker = ticker;
        this.nome = nome;
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public String getTicker() {
        return ticker;
    }

    public String getNome() {
        return nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    
            
}
