class Keys{
    IntList pressed = new IntList();
    HashMap<String,Integer> keys_by_name = new HashMap<String,Integer>();

    Keys(){
        keys_by_name.put("spacebar", 32);
        keys_by_name.put("left control", 17);
        keys_by_name.put("option", 18);
        keys_by_name.put("command", 157);
        keys_by_name.put("shift", 16);
        keys_by_name.put("delete", 127);
        keys_by_name.put("z", 90);
    }

    void add(int key){
        if(isnt_pressed(key)) this.pressed.append(key);
    }

    void remove(int key){
        if(is_pressed(key)) this.pressed.remove(index(key));
    }

    String to_string(){
        String output = "";
        for(int k=0;k<pressed.size();k++){
            output += pressed.get(k) + ": " + key_name(pressed.get(k)) + "; ";
        }
        return output;
    }

    String key_name(int key_code){
        String output = "Key name not found";
        for(Map.Entry entry : keys_by_name.entrySet()){
            if(key_code == (Integer)entry.getValue()) output = (String)entry.getKey();
        }
        return output;
    }

    int index(int key_code){
        Integer key_index = -1;
        for(int i=0;i<pressed.size();i++){
            int key = pressed.get(i);
            if(key_code==key){
                key_index = i;
            }
        }
        return key_index;
    }

    Boolean are_pressed(String... key_names){
        Boolean output = is_pressed(key_names[0]);
        for(int i=1;i<key_names.length;i++){
            output = is_pressed(key_names[i]) && is_pressed(key_names[i-1]);
        }
        return output;
    }

    Boolean arent_pressed(String... key_names){
        return !this.are_pressed(key_names);
    }

    Boolean is_pressed(String key_name){
        return this.is_pressed(this.keys_by_name.get(key_name));
    }

    Boolean is_pressed(int key_code){
        return this.index(key_code) > -1;    
    }

    Boolean isnt_pressed(int key_code){
        return this.index(key_code) == -1;    
    }
}