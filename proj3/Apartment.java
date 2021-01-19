public class Apartment {

  private String address;
  private String aptNumber;
  private String city;
  private int zipcode;
  private double monthlyCost;
  private int sqFootage;

  public Apartment(String addy, String aptNum, String c, int zip, double cost, int sqFt){
    setAddress(addy);
    setAptNum(aptNum);
    setCity(c);
    setZip(zip);
    setCost(cost);
    setSqFootage(sqFt);
  }

  public String getCity(){
    return city;
  }

  public void setCity(String c){
    this.city = c;
  }

  public String getAddress(){
    return address;
  }

  public void setAddress(String ad){
    this.address = ad;
  }

  public String getAptNum(){
    return aptNumber;
  }

  public void setAptNum(String aptNum){
    this.aptNumber = aptNum;
  }

  public int getZip(){
    return zipcode;
  }

  public void setZip(int zc){
    this.zipcode = zc;
  }

  public void setCost(double cost){
    this.monthlyCost = cost;
  }

  public double getCost(){
    return monthlyCost;
  }

  public int getSqFootage(){
    return sqFootage;
  }

  public void setSqFootage(int sqft){
    this.sqFootage = sqft;
  }

  public String getKey(){
    return address + aptNumber + zipcode;
  }

  public String aptToString(){
    return "Apt:" + address + ", Unit:" + aptNumber + ", City:" + city + ", ZIP:" + zipcode +
    ", Price/Month:$" + String.format("%.2f", monthlyCost) + ", Square Footage:" + sqFootage + "sqft.";
  }
}
