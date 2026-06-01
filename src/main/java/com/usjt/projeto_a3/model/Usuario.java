
package com.usjt.projeto_a3.model;

public class Usuario {
    private int id;
    private String nome;
    private String email;
    private String senha;
    private String perfil; //Admin ou User
    private String status;

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
    private int dataNascimento;
    
    public Usuario(){
        
    }
    public Usuario(int id, String nome, String email, String senha, String perfil, String status) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.perfil = perfil;
        this.status = status;
    }

    public int getDataNascimento() {
        return dataNascimento;
    }
    
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDataNascimento(int dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    @Override
    public String toString() {
        return "Usuario{" + "id=" + id + ", nome=" + nome + ", email=" + email + ", senha=" + senha + ", perfil=" + perfil + '}';
    }
    
    
}
