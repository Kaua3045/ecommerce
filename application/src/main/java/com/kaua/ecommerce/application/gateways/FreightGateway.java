package com.kaua.ecommerce.application.gateways;

import com.kaua.ecommerce.application.gateways.commands.CalculateFreightInput;
import com.kaua.ecommerce.application.gateways.commands.ListFreightsInput;
import com.kaua.ecommerce.domain.freight.Freight;

import java.util.List;

public interface FreightGateway {

    Freight calculateFreight(CalculateFreightInput command);

    List<Freight> listFreights(ListFreightsInput command);
}
