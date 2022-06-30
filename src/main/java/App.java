import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class App {
    private ItemRepository itemRepository;
    private SalesPromotionRepository salesPromotionRepository;

    public App(ItemRepository itemRepository, SalesPromotionRepository salesPromotionRepository) {
        this.itemRepository = itemRepository;
        this.salesPromotionRepository = salesPromotionRepository;
    }

    public String bestCharge(List<String> inputs) {
        //TODO: write code here
        List<Integer> numbers = new ArrayList<Integer>();
        List<Item> items = new ArrayList<Item>();
        for(int i=0;i<inputs.size();i++){
            String str = inputs.get(i);
            str = str.replace(" ", "");
            int index = str.indexOf("x");
            String itemId = str.substring(0,index);
            Item item = itemRepository.findAll().stream().filter(s->s.getId().equals(itemId)).findFirst().get();
            items.add(item);
            int number = Integer.parseInt(str.substring(index+1));
            numbers.add(number);
        }

        int total = 0,save = 0;
        List<String> relatedItems = new ArrayList<String>();
        SalesPromotion salesPromotion =salesPromotionRepository
                .findAll()
                .stream()
                .filter(s->s.getType().equals("50%_DISCOUNT_ON_SPECIFIED_ITEMS"))
                .findFirst()
                .get();
        relatedItems = salesPromotion.getRelatedItems();
        List<String> names = new ArrayList<String>();
        String receiptString = "============= Order details =============\n";
        for(int i=0;i<items.size();i++){
            double price = 0;
            for (int j = 0; j < relatedItems.size(); j++) {
                if (items.get(i).getId().equals(relatedItems.get(j))) {
                    names.add(items.get(i).getName());
                    price = items.get(i).getPrice() * 0.5;
                    save+= price*numbers.get(i);
                }
            }
            int money= (int) (items.get(i).getPrice()*numbers.get(i));
            total+= money;
            receiptString = receiptString+items.get(i).getName()+" x "+numbers.get(i)+" = "+money+" yuan\n";
        }

        String displayName = "";
        if(total>=30){
            if(save>6){
                total = total-save;
                displayName="Half price for certain dishes (";
                String result = names.stream().map(String::valueOf).collect(Collectors.joining(","));
                displayName=displayName+result+"),";
            }else{
                total-=6;
                save =6;
                displayName="Deduct 6 yuan when the order reaches 30 yuan,";
            }
        }else{
            if(save>0){
                total = total-save;
                displayName="Half price for certain dishes (";
                String result = names.stream().map(String::valueOf).collect(Collectors.joining(","));
                displayName=displayName+result+"),";
            }
        }

        if(save>0) {
            receiptString = receiptString+"-----------------------------------\n"+"Promotion used:\n";
            receiptString = receiptString + displayName + "saving " + save + " yuan\n";
        }
        receiptString=receiptString+ "-----------------------------------\n" + "Total: "+total+" yuan\n"+"===================================";

        return receiptString;
    }
}
