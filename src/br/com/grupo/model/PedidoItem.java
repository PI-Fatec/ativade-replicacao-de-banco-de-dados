package br.com.grupo.model;

import java.math.BigDecimal;

public class PedidoItem {
    private int id;
    private int pedidoId;
    private int produtoId;
    private Produto produto; // para JOINs
    private int quantidade;
    private BigDecimal valorUnitario;

    public PedidoItem() {}

    public PedidoItem(int id, int pedidoId, int produtoId, int quantidade, BigDecimal valorUnitario) {
        this.id = id;
        this.pedidoId = pedidoId;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPedidoId() { return pedidoId; }
    public void setPedidoId(int pedidoId) { this.pedidoId = pedidoId; }

    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) { this.valorUnitario = valorUnitario; }

    @Override
    public String toString() {
        return "PedidoItem " + id +
                (produto != null ? "\nProduto " + produto.getDescricao() : "") +
                "\nQuantidade " + quantidade;
    }
}
