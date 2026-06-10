package br.com.grupo.model;

import java.math.BigDecimal;
import java.util.Date;

public class Pedido {
    private int id;
    private int clienteId;
    private Cliente cliente; // para JOINs
    private BigDecimal valorTotal;
    private String status;
    private Date criadoEm;
    private String criadoPor;

    public Pedido() {}

    public Pedido(int id, int clienteId, BigDecimal valorTotal, String status, Date criadoEm, String criadoPor) {
        this.id = id;
        this.clienteId = clienteId;
        this.valorTotal = valorTotal;
        this.status = status;
        this.criadoEm = criadoEm;
        this.criadoPor = criadoPor;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCriadoEm() { return criadoEm; }
    public void setCriadoEm(Date criadoEm) { this.criadoEm = criadoEm; }

    public String getCriadoPor() { return criadoPor; }
    public void setCriadoPor(String criadoPor) { this.criadoPor = criadoPor; }

    @Override
    public String toString() {
        return "Pedido " + id +
                (cliente != null ? "\nCliente " + cliente.getNome() : "") +
                "\nValor Total " + valorTotal +
                "\nStatus " + status;
    }
}
