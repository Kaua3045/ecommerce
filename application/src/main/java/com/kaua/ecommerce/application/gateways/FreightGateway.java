package com.kaua.ecommerce.application.gateways;

import com.kaua.ecommerce.application.gateways.commands.CalculateFreightCommand;
import com.kaua.ecommerce.application.gateways.commands.ListFreightsCommand;
import com.kaua.ecommerce.domain.freight.Freight;

import java.util.List;

public interface FreightGateway {

    Freight calculateFreight(CalculateFreightCommand command);

    List<Freight> listFreights(ListFreightsCommand command);
}
