package br.com.grupo.model;

import java.math.BigDecimal;

public class RelatorioVendas {
    private int quantidadeTotalPedidos;
    private BigDecimal valorMedioPedidos;
    private BigDecimal valorTotalVendido;

    public RelatorioVendas() {}

    public RelatorioVendas(int quantidadeTotalPedidos, BigDecimal valorMedioPedidos, BigDecimal valorTotalVendido) {
        this.quantidadeTotalPedidos = quantidadeTotalPedidos;
        this.valorMedioPedidos = valorMedioPedidos;
        this.valorTotalVendido = valorTotalVendido;
    }

    public int getQuantidadeTotalPedidos() { return quantidadeTotalPedidos; }
    public void setQuantidadeTotalPedidos(int quantidadeTotalPedidos) { this.quantidadeTotalPedidos = quantidadeTotalPedidos; }

    public BigDecimal getValorMedioPedidos() { return valorMedioPedidos; }
    public void setValorMedioPedidos(BigDecimal valorMedioPedidos) { this.valorMedioPedidos = valorMedioPedidos; }

    public BigDecimal getValorTotalVendido() { return valorTotalVendido; }
    public void setValorTotalVendido(BigDecimal valorTotalVendido) { this.valorTotalVendido = valorTotalVendido; }

    @Override
    public String toString() {
        return "Quantidade Total de Pedidos: " + quantidadeTotalPedidos +
                "\nValor Médio dos Pedidos: R$ " + valorMedioPedidos +
                "\nValor Total Vendido: R$ " + valorTotalVendido;
    }
}
