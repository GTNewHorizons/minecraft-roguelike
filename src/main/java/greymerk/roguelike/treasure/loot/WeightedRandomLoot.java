package greymerk.roguelike.treasure.loot;


import greymerk.roguelike.util.IWeighted;

import java.util.Random;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.google.gson.JsonObject;

public class WeightedRandomLoot implements Comparable<WeightedRandomLoot>, IWeighted<ItemStack>{
	
	private Item id;
	private int damage;
	
	private int min;
	private int max;
	
	private int weight;
	
	public WeightedRandomLoot(Item id, int damage, int minStackSize, int maxStackSize, int weight){
		this.id = id;
		this.damage = damage;
		this.min = minStackSize;
		this.max = maxStackSize;
		this.weight = weight;
	}

	public WeightedRandomLoot(JsonObject json){
		id = Item.getItemById(json.get("id").getAsInt());
		damage = json.has("meta") ? json.get("meta").getAsInt() : 0;
		weight = json.get("weight").getAsInt();

		if(json.has("min") && json.has("max")){
			min = json.get("min").getAsInt();
			max = json.get("max").getAsInt();	
		} else {
			min = 1;
			max = 1;
		}		
	}
	
	
	public WeightedRandomLoot(Item id, int damage, int weight){
		this(id, damage, 1, 1, weight);
	}	
	
	private int getStackSize(Random rand){
		if (max == 1) return 1;
		
		return rand.nextInt(max - min) + min;
	}

	
	@Override
	public int getWeight(){
		return this.weight;
	}

	@Override
	public ItemStack get(Random rand) {
		return new ItemStack(id, this.getStackSize(rand), damage);
	}


	@Override
	public int compareTo(WeightedRandomLoot other) {
		if (this.weight > other.weight) return -1;
		if (this.weight < other.weight) return 1;
		
		return 0;
	}
}