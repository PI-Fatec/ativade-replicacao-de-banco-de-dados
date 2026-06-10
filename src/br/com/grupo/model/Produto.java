package br.com.grupo.model;

import java.math.BigDecimal;
import java.util.Date;

public class Produto {
    private int id;
    private String descricao;
    private String categoria;
    private BigDecimal valor;
    private int estoque;
    private Date criadoEm;
    private String criadoPor;

    public Produto() {}

    public Produto(int id, String descricao, String categoria, BigDecimal valor, int estoque, Date criadoEm, String criadoPor) {
        this.id = id;
        this.descricao = descricao;
        this.categoria = categoria;
        this.valor = valor;
        this.estoque = estoque;
        this.criadoEm = criadoEm;
        this.criadoPor = criadoPor;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public int getEstoque() { return estoque; }
    public void setEstoque(int estoque) { this.estoque = estoque; }

    public Date getCriadoEm() { return criadoEm; }
    public void setCriadoEm(Date criadoEm) { this.criadoEm = criadoEm; }

    public String getCriadoPor() { return criadoPor; }
    public void setCriadoPor(String criadoPor) { this.criadoPor = criadoPor; }

    @Override
    public String toString() {
        return "Produto{" +
                "id=" + id +
                ", descricao='" + descricao + '\'' +
                ", valor=" + valor +
                ", estoque=" + estoque +
                '}';
    }
}
