/*
  Heap PQ for Apartments
*/
public class AptPQ{
  private Apartment[] price;  //Min PQ array
  private Apartment[] size;   //maxPQ array
  private int aptCount;       //number of apartments in PQ
  private int initSize = 50;  //initial size for array

  private SeparateChainingHashST<String, Apartment> priceCity;  //minPQ hash tables for price w/ specified city
  private SeparateChainingHashST<String, Apartment> sizeCity;   //maxPQ hash table for size w/ specified city

  private SeparateChainingHashST<String, Integer> priceIndex;   //minPQ hash tables for price for whole set
  private SeparateChainingHashST<String, Integer> sizeIndex;    //maxPQ hash table for size for whole set

  public AptPQ(){
    price = new Apartment[initSize];
    size = new Apartment[initSize];
    priceIndex = new SeparateChainingHashST<String, Integer>();
    sizeIndex = new SeparateChainingHashST<String, Integer>();
    priceCity = new SeparateChainingHashST<String, Apartment>();
    sizeCity = new SeparateChainingHashST<String, Apartment>();
    aptCount = 0;
  }

  public void add(Apartment apt){
    //just in case, but I initialized the arrays to be quite large
    //considering
    if (aptCount == initSize){
      //resize the array
      Apartment[] temp1 = new Apartment[initSize*2];
      Apartment[] temp2 = new Apartment[initSize*2];
      for(int i = 0; i<initSize; i++){
          temp1[i] = price[i];
          temp2[i] = size[i];
      }
      initSize *= 2;
      price = temp1;
      size = temp2;
    }

    //otherwise add apartments to the arrays
    price[aptCount] = apt;
    size[aptCount] = apt;

    //put the apartment in the indexible hash tables
    String key = apt.getKey();
    priceIndex.put(key, aptCount);
    sizeIndex.put(key, aptCount);
    //get the apartment to the correct position in the arrays
    swimPrice(aptCount);
    swimSqFeet(aptCount);
    aptCount++; //increase count for next addition

    if (!priceCity.contains(apt.getCity()) || priceCity.get(apt.getCity()).getCost() > apt.getCost()){
      priceCity.delete(apt.getCity());
      priceCity.put(apt.getCity(), apt);
    }

    if (!sizeCity.contains(apt.getCity()) || sizeCity.get(apt.getCity()).getSqFootage() < apt.getSqFootage()){
      sizeCity.delete(apt.getCity());
      sizeCity.put(apt.getCity(), apt);
    }

  }

  public boolean remove(Apartment apt){
    //if the PQ contains this apartment
    if (priceIndex.contains(apt.getKey())){
      int index = priceIndex.get(apt.getKey());
      Apartment toRemove = price[index];
      aptCount--; //decremeent the number of apartments
      exch(index, aptCount, true); //swap in the price arrays
      price[aptCount] = null; //set the last postion to empty
      priceIndex.delete(apt.getKey()); //delete the associated indirection hash table entry
      //reposition the entries in the PQ
      swimPrice(index);
      sinkPrice(index);

      //if the apartment to remove is the cheapest in that city
      if (priceCity.get(toRemove.getCity()).equals(toRemove)){
        //delete it
        priceCity.delete(toRemove.getCity());
        for (int i = 0; i < aptCount; i ++){
          //for each apartment, if the apartment at that index is in the same city as the removed
          // AND the price hash table doesn't contain that city, OR the cost
          //at that city is greater than the cheapest overall
          if(price[i].getCity().equals(toRemove.getCity()) && (!priceCity.contains(price[i].getCity()) || priceCity.get(price[i].getCity()).getCost() > price[i].getCost())){
            priceCity.delete(price[i].getCity());
            priceCity.put(price[i].getCity(), price[i]);
          }
        }
      }

      //same process for size
      index = sizeIndex.get(apt.getKey());
      toRemove = size[index];
      exch(index, aptCount, false); //swap in the size arrays
      size[aptCount] = null; //set the last postion to empty
      sizeIndex.delete(apt.getKey()); //delete the associated indirection hash table entry
      //reposition the entries in the PQ
      swimSqFeet(index);
      sinkSqFeet(index);

      if (sizeCity.get(toRemove.getCity()).equals(toRemove)){
        sizeCity.delete(toRemove.getCity());
        for (int i = 0; i < aptCount; i ++){
          if(size[i].getCity().equals(toRemove.getCity()) && (!sizeCity.contains(size[i].getCity()) || sizeCity.get(size[i].getCity()).getSqFootage() < size[i].getSqFootage())){
            sizeCity.delete(size[i].getCity());
            sizeCity.put(size[i].getCity(), size[i]);
          }
        }
      }
      return true;
    }
    return false;
  }

  public boolean edit(Apartment apt){
    //make sure that the apartment actuall exisits in the PQ
    if(priceIndex.contains(apt.getKey())){
        int index = priceIndex.get(apt.getKey()); //found index of the apartment to edit
        double oldPrice = price[index].getCost(); //previous cost
        price[index].setCost(apt.getCost()); //change the price in the array to the new one
        Apartment edited = price[index]; //place holder for the edited apartment

        //place the apartment in the correct positions in the PQs
        swimPrice(index);
        sinkPrice(index);

        //if the lowest price apartment in the given city is the edited apartment
        if(priceCity.get(edited.getCity()).equals(edited)){
            //and if the edited cost is less than the original cost
            if(edited.getCost() < oldPrice){
                //update
                priceCity.delete(edited.getCity());
                priceCity.put(edited.getCity(), edited);
            }else{
                //otherwise
                priceCity.delete(edited.getCity()); //delete that cheapest in that city
                for(int i = 0; i < aptCount; i++){
                    //and for each index in the price array
                    //if the apartment at that index is in the same city as the edited
                    //AND the price hash tbale doesn't contain htat city OR the
                    //cheapest apartment in that city is greater than the cheapest apartment overall
                    if(price[i].getCity().equals(edited.getCity()) && (!priceCity.contains(edited.getCity()) || priceCity.get(edited.getCity()).getCost() > price[i].getCost())){
                        priceCity.delete(price[i].getCity());
                        priceCity.put(price[i].getCity(), price[i]);
                    }
                }
            }
        //if the new cost is less than the lowest cost for that city
        }else if(edited.getCost() < priceCity.get(edited.getCity()).getCost()){
            //delete the old entry and update
            priceCity.delete(edited.getCity());
            priceCity.put(edited.getCity(), edited);
        }
        //set updated the cost in the size array
        size[sizeIndex.get(apt.getKey())].setCost(apt.getCost());
        //if the biggest apartment in the given city is the same as the edited
        if(sizeCity.get(edited.getCity()).equals(edited)){
            sizeCity.delete(edited.getCity());
            sizeCity.put(edited.getCity(),edited);
        }
      return true;
    }
    return false;
  }

  //retrieving max and min
  public String getMinPrice(){
    return price[0].aptToString();
  }

  public String getMaxSqFeet(){
    return size[0].aptToString();
  }

  //overload methods for when it depends on the city
  public String getMinPrice(String city){
    return priceCity.get(city).aptToString();
  }

  public String getMaxSqFeet(String city){
    return sizeCity.get(city).aptToString();
  }

  public void exch(int i, int j, boolean b){
    if(b){ //price array
        //clear the keys of the current positions in the index hash table
        priceIndex.delete(price[i].getKey());
        priceIndex.delete(price[j].getKey());
        //swap them
        Apartment swap = price[i];
        price[i] = price[j];
        price[j] = swap;
        //put the newly swapped keys in their updated places
        priceIndex.put(price[i].getKey(), i);
        priceIndex.put(price[j].getKey(), j);
    }else{ //size array
        //same as above but for size
        sizeIndex.delete(size[i].getKey());
        sizeIndex.delete(size[j].getKey());
        Apartment swap = size[i];
        size[i] = size[j];
        size[j] = swap;
        sizeIndex.put(size[i].getKey(), i);
        sizeIndex.put(size[j].getKey(), j);
    }
  }

  //sink and swim for price Min PQ
  public void sinkPrice(int pos){
    while (2*(pos+1) < aptCount) {
        int j = 2*(pos+1);
        if (j < aptCount && price[j] != null && price[j-1] != null && price[j].getCost() < price[j-1].getCost()){
          j++;
        }
        if (!(price[j-1] != null && price[pos] != null && price[j-1].getCost() < price[pos].getCost())){
          break;
        }
        exch(pos, j-1, true);
        pos = j-1;
    }
  }

  public void swimPrice(int pos){
    while(pos > 0 && price[pos] != null && price[(pos-1)/2] != null && price[pos].getCost() < price[(pos-1)/2].getCost()){
      exch(pos, (pos-1)/2, true);
      pos = (pos-1)/2;
    }
  }

  //sink and swim for square footage Max PQ
  public void sinkSqFeet(int pos){
    while (2*(pos+1) < aptCount) {
        int j = 2*(pos+1);
        if (j < aptCount && size[j] != null && size[j-1] != null && size[j].getSqFootage() > size[j-1].getSqFootage()){
          j++;
        }
        if (!(size[j-1] != null && size[pos] != null && size[j-1].getSqFootage() > size[pos].getSqFootage())){
          break;
        }
        exch(pos, j-1, false);
        pos = j-1;
    }
  }

  public void swimSqFeet(int pos){
    while(pos > 0 && size[pos] != null && size[(pos-1)/2] != null && size[pos].getSqFootage() > size[(pos-1)/2].getSqFootage()){
      exch(pos, (pos-1)/2, false);
      pos = (pos-1)/2;
    }
  }
}
