package com.tis.mx.application.service.impl;

import com.tis.mx.application.dto.InitialInvestmentDto;
import com.tis.mx.application.dto.InvestmentYieldDto;
import com.tis.mx.application.service.CompoundInterestCalculator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CompoundInterestCalculatorImpl implements CompoundInterestCalculator {

  
  @Override
  public List<InvestmentYieldDto> createRevenueGrid(InitialInvestmentDto initialInvestment) {

	    List<InvestmentYieldDto> tablaDeRendimiento = new ArrayList<>();

	    int ciclosDeInversion = initialInvestment.getInvestmentYears();

	    for (int ciclo = 0; ciclo < ciclosDeInversion; ciclo++) {

	      InvestmentYieldDto rendimientoAnual = null;
	      
	      if (ciclo == 0) {
	        rendimientoAnual =
	            this.calcularRendimientoAnual(initialInvestment, null);
	      } else { 
	        
	        rendimientoAnual =
	            this.calcularRendimientoAnual(initialInvestment, tablaDeRendimiento.get(ciclo - 1));
	      }
	      
	      tablaDeRendimiento.add(rendimientoAnual);      
	    }


	    return tablaDeRendimiento;
	  }


	  private InvestmentYieldDto calcularRendimientoAnual(InitialInvestmentDto inversionInicial,
	      InvestmentYieldDto rendimientoAnterior) {
	    
	    InvestmentYieldDto rendimiento = new InvestmentYieldDto();
	    
	    if (rendimientoAnterior == null) {
	      /**
	       * Aqui no existe rendimiento anterior
	       */
	      rendimiento.setInvestmentYear(1); 
	      rendimiento.setInitialInvestment(inversionInicial.getInitialInvestment());
	      rendimiento.setYearlyInput(inversionInicial.getYearlyInput());
	    } else {
	      /**
	       * Aqui si hay un rendimiento anterior
	       */
	       rendimiento.setInvestmentYear(rendimientoAnterior.getInvestmentYear() + 1);
	       rendimiento.setInitialInvestment(rendimientoAnterior.getFinalBalance());
	       
	       Double aportacion = rendimientoAnterior.getYearlyInput() * 
	           (1 + (inversionInicial.getYearlyInputIncrement() / 100));
	       rendimiento.setYearlyInput(aportacion);
	    }
	    
	    Double rendimientoAnual = rendimiento.getInitialInvestment() + rendimiento.getYearlyInput();
	    rendimientoAnual = rendimientoAnual * (inversionInicial.getInvestmentYield() / 100);
	    
	    rendimiento.setInvestmentYield(rendimientoAnual);
	    
	    
	    Double saldoFinal = rendimiento.getInitialInvestment() 
	        + rendimiento.getYearlyInput() + rendimiento.getInvestmentYield();
	    
	    rendimiento.setFinalBalance(saldoFinal);

	    return rendimiento;
	  }

  @Override
  public boolean validateInput(InitialInvestmentDto initialInvestment) {

    this.setDefaults(initialInvestment);
    boolean cumple = true;

    cumple = cumple && (initialInvestment.getInitialInvestment() >= 1000);
    cumple = cumple && (initialInvestment.getYearlyInput() >= 0.0);
    cumple = cumple && (initialInvestment.getYearlyInputIncrement() >= 0);
    cumple = cumple && (initialInvestment.getInvestmentYears() > 0.0);
    cumple = cumple && (initialInvestment.getInvestmentYield() > 0.0);

    return cumple;
  }

  private void setDefaults(InitialInvestmentDto initialInvestment) {
    Double yearlyInput = initialInvestment.getYearlyInput();
    yearlyInput = yearlyInput == null ? 0.0 : yearlyInput;
    initialInvestment.setYearlyInput(yearlyInput);

    Integer yearlyInputIncrement = initialInvestment.getYearlyInputIncrement();
    yearlyInputIncrement = yearlyInputIncrement == null ? 0 : yearlyInputIncrement;
    initialInvestment.setYearlyInputIncrement(yearlyInputIncrement);
  }


}
