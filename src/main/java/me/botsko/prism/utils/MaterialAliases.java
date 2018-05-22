package me.botsko.prism.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import me.botsko.prism.Prism;
import me.botsko.prism.database.mysql.IdMapQuery;

public class MaterialAliases {
	/**
	 * Contains loaded item ids => aliases
	 */
	protected HashMap<String, String> itemAliases = new HashMap<String, String>();

	/**
	 * Load the yml file and save config to hashmap
	 * 
	 * @param plugin
	 */

	protected EnumSet<Material> nonData = EnumSet.noneOf(Material.class);

	public MaterialAliases() {

		FileConfiguration items = null;
		InputStream defConfigStream = this.getClass().getResourceAsStream("/items.yml");
		if (defConfigStream != null) {
			System.out.println("Elixr: Loaded items directory");
			items = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
		}

		if (items != null) {

			// Load all item ids/aliases
			Map<String, Object> itemaliases = items.getConfigurationSection("items").getValues(false);

			// Cache the values for easier lookup
			if (itemaliases != null) {
				for (Map.Entry<String, Object> entry : itemaliases.entrySet()) {
					@SuppressWarnings("unchecked")
					ArrayList<String> aliases = (ArrayList<String>) entry.getValue();
					if (aliases.size() > 0) {
						for (String alias : aliases) {
							itemAliases.put(entry.getKey().toLowerCase(), alias);
						}
					}
				}
			}
		} else {
			System.out.println("ERROR: The Elixr library was unable to load an internal item alias list.");
		}

		for (Material material : Material.values()) {
			if (!material.isBlock())
				nonData.add(material);
		}
	}

	public void initAllMaterials() {
		initMaterials(Material.values());
	}

	public void initMaterials(Material... materials) {
		IdMapQuery query = new IdMapQuery();

		for (Material m : materials) {
			String matName = m.name().toLowerCase(Locale.ENGLISH);

			int max = 1;
			if (m.isBlock())
				max = 16;

			for (int x = 0; x < max; ++x) {
				int fx = x;
				String key = matName + ":" + x;

				query.findIds(m.name().toLowerCase(Locale.ENGLISH), String.valueOf(x), (i, d) -> {
					idCache.put(key, i + ":" + d);
					matCache.put(i + ":" + d, key);
				}, () -> {
					int id = 0;
					int data = fx;

					try {
						id = (Integer) Material.class.getMethod("getId").invoke(m);
						query.map(matName, String.valueOf(fx), id, data);
					} catch (Exception e) {
						id = query.mapAutoId(matName, String.valueOf(fx));
					}

					idCache.put(key, id + ":" + data);
					matCache.put(id + ":" + data, key);
				});
			}
		}
	}

	// TODO: org.bukkit.block.data.BlockData in 1.13
	public static class MaterialState {
		protected MaterialState() {
		}

		public MaterialState(Material material, String state) {
			this.material = material;
			this.state = state;
		}

		public Material material;
		public String state;

		@SuppressWarnings("deprecation")
		public MaterialData asData() {
			return new MaterialData(material, Byte.parseByte(state));
		}

		public ItemStack asItem() {
			return new ItemStack(material, 1, Short.parseShort(state));
		}
	}

	private Map<String, String> matCache = new HashMap<>();
	private Map<String, String> idCache = new HashMap<>();
	
	private void storeCache(Material material, String state, int block_id, int block_subid) {
		String matKey = material.name().toLowerCase(Locale.ENGLISH) + ":" + state;
		String idKey = block_id + ":" + block_subid;
		
		matCache.put(idKey, matKey);
		idCache.put(matKey, idKey);
	}
	
	private MaterialState fromCache(int block_id, int block_subid) {
		String value = matCache.get(block_id + ":" + block_subid);
		
		if(value != null) {
			String[] parts = value.split(":", 2);
			
			return new MaterialState(Material.matchMaterial(parts[0]), parts[1]);
		}
		
		return null;
	}

	public MaterialState idsToMaterial(int block_id, int block_subid) {
		MaterialState cachedMaterial = fromCache(block_id, block_subid);

		if (cachedMaterial != null)
			return cachedMaterial;

		MaterialState result = new MaterialState();
		IdMapQuery query = new IdMapQuery();

		query.findMaterial(block_id, block_subid, (material, state) -> {
			result.material = Material.matchMaterial(material.toUpperCase(Locale.ENGLISH));
			result.state = state;
			
			storeCache(result.material, result.state, block_id, block_subid);
		}, () -> {
			Material material;
			try {
				material = (Material) Material.class.getMethod("getMaterial", int.class).invoke(null, block_id);
			} catch (Exception e) {
				material = null;
				e.printStackTrace();
			}

			if (material != null) {
				String materialName = material.name().toLowerCase(Locale.ENGLISH);
				String state = String.valueOf(block_subid);
				
				query.map(materialName, state, block_id, block_subid);

				// TODO: Proper state handling 1.13
				result.material = material;
				result.state = state;
				
				storeCache(result.material, result.state, block_id, block_subid);
			} else
				Prism.log("matError: [" + block_id + ", " + block_subid + "] -> ???");
		});

		if (result.material == null) {
			return null;
		}

		return result;
	}
	
	private int[] fromCache(Material material, String state) {
		String value = idCache.get(material.name().toLowerCase(Locale.ENGLISH) + ":" + state);
		
		if(value != null) {
			String[] parts = value.split(":", 2);
			int[] ids = { Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) };
			
			return ids;
		}
		
		return null;
	}

	public int[] materialToIds(Material material, String state) {
		int block_subid;
		
		// For tools, where durability doesn't mean a different item (different cached value) but is still important
		int durability = 0;
		try { durability = Integer.parseInt(state); } catch(NumberFormatException e) {}
		
		if(material.getMaxDurability() > 0)
			block_subid = 0;
		else
			block_subid = durability;
		
		int[] cachedIds = fromCache(material, block_subid == 0 ? state : String.valueOf(block_subid));

		if (cachedIds != null)
			return cachedIds;

		int[] result = {0, 0};
		IdMapQuery query = new IdMapQuery();
		String materialName = material.name().toLowerCase(Locale.ENGLISH);

		query.findIds(materialName, state, (query_id, query_subid) -> {
			result[0] = query_id;
			result[1] = query_subid;
			
			storeCache(material, state, query_id, query_subid);
		}, () -> {
			int block_id = 0;

			try {
				block_id = (Integer) Material.class.getMethod("getId").invoke(material);
				query.map(materialName, state, block_id, block_subid);
				
				result[0] = block_id;
				result[1] = block_subid;
				
				storeCache(material, state, block_id, block_subid);
			} catch (Exception e) {
				block_id = query.mapAutoId(materialName, state);
				
				result[0] = block_id;
				result[1] = 0;
				
				storeCache(material, state, block_id, 0);
			}
		});
		
		if(block_subid != durability)
			result[1] = durability;
		
		return result;
	}
	
	public static final int SUBID_WILDCARD = -1;
	
	public int[] materialToIdsWildcard(Material material, String state) {
		int[] ids = materialToIds(material, state);
		
		if(material.getMaxDurability() > 0)
			ids[1] = SUBID_WILDCARD;
		
		return ids;
	}

	/**
	 * Returns the loaded list of item aliases/ids;
	 * 
	 * @return
	 */
	public HashMap<String, String> getItemAliases() {
		return itemAliases;
	}

	/**
	 * Returns the proper name given an item type id, data/durability
	 * 
	 * @param typeid
	 * @param subid
	 * @return
	 */
	@Deprecated
	public String getAlias(int typeid, int subid) {
		return getAlias(Material.getMaterial(typeid), subid);
	}

	@SuppressWarnings("deprecation")
	public String getAlias(Material material, int subid) {
		String item_name = null;
		if (!itemAliases.isEmpty()) {
			String key = material.name().toLowerCase() + ":" + subid;
			item_name = itemAliases.get(key);
			// TODO: Remove this for 1.13
			if (item_name == null)
				item_name = itemAliases.get(material.getId() + ":" + subid);
		}
		if (item_name == null) {
			ItemStack i = new ItemStack(material, subid);
			item_name = i.getType().name().toLowerCase().replace("_", " ");
		}
		return item_name;
	}

	/**
	 * Returns the proper name given an item stack
	 * 
	 * @param i
	 * @return
	 */
	public String getAlias(ItemStack i) {
		return getAlias(i.getType(), i.getDurability());
	}

	/**
	 * 
	 * @param alias
	 * @return
	 */
	@Deprecated
	public ArrayList<int[]> getIdsByAlias(String alias) {
		ArrayList<int[]> itemIds = new ArrayList<int[]>();
		if (!itemAliases.isEmpty()) {
			for (Entry<String, String> entry : itemAliases.entrySet()) {
				int[] ids = new int[2];
				if (entry.getValue().equals(alias)) {
					String[] _tmp = entry.getKey().split(":");
					ids[0] = Integer.parseInt(_tmp[0]);
					ids[1] = Integer.parseInt(_tmp[1]);
					itemIds.add(ids);
				}
			}
		}
		return itemIds;
	}

	// TODO: Break this in 1.13. Woooo.
	@SuppressWarnings("deprecation")
	public ArrayList<MaterialData> getMaterialsByAlias(String alias) {
		ArrayList<MaterialData> itemIds = new ArrayList<>();
		if (!itemAliases.isEmpty()) {
			for (Entry<String, String> entry : itemAliases.entrySet()) {
				if (entry.getValue().equals(alias)) {
					String[] _tmp = entry.getKey().split(":");
					Material m = Material.matchMaterial(_tmp[0]);
					byte b = Byte.parseByte(_tmp[1]);
					itemIds.add(new MaterialData(m, b));
				}
			}
		}
		return itemIds;
	}
}