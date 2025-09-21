package it.unibs.ingsw.destinazioni.application.port.in.areaofinterest;

import java.util.List;
import java.util.Map;

/**
 * Interface per la consultazione delle aree di interesse configurate nel sistema.
 */
/*@ public invariant isEmpty() <==> townList().isEmpty();
  @ public invariant isEmpty() <==> townProvinceMap().isEmpty();
  @ public invariant townList().size() == townProvinceMap().size();
  @ public invariant (\forall String town; townList().contains(town);
  @                   townProvinceMap().containsKey(town));
  @*/
public interface QueryAreaOfInterestUseCase {

  /**
   * Verifica se non ci sono aree di interesse configurate.
   * 
   * @return true se non ci sono aree configurate, false altrimenti
   */
  /*@ ensures \result <==> townList().isEmpty();
    @ ensures \result <==> townProvinceMap().isEmpty();
    @ pure
    @*/
  boolean isEmpty();


  /**
   * Ottiene la lista di tutti i comuni configurati come aree di interesse.
   * 
   * @return lista dei nomi dei comuni
   */
  /*@ ensures \result != null;
    @ ensures (\forall String town; \result.contains(town);
    @          town != null && !town.trim().isEmpty());
    @ ensures \result.size() == townProvinceMap().size();
    @ ensures (\forall String town; \result.contains(town);
    @          townProvinceMap().containsKey(town));
    @ pure
    @*/
  List<String> townList();


  /**
   * Ottiene la mappa che associa ogni comune alla sua provincia.
   * 
   * @return mappa comune -> provincia
   */
  /*@ ensures \result != null;
    @ ensures (\forall String town; \result.keySet().contains(town);
    @          town != null && !town.trim().isEmpty());
    @ ensures (\forall String province; \result.values().contains(province);
    @          province != null && !province.trim().isEmpty());
    @ ensures \result.size() == townList().size();
    @ ensures (\forall String town; townList().contains(town);
    @          \result.containsKey(town));
    @ pure
    @*/
  Map<String, String> townProvinceMap();
}
