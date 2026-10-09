package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Vehicule;

import java.util.List;

public interface IVehiculeService {

    Vehicule addVehicule(Vehicule vehicule);

    Vehicule updateVehicule(Long id, Vehicule vehicule);

    Vehicule getVehiculeById(Long id);

    List<Vehicule> getAllVehicules();

    void deleteVehicule(Long id);
}
