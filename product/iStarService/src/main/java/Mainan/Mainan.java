package Mainan;

import Model.ReferencePair;

import java.util.HashMap;

public class Mainan {
    public static void main(String[] args){
        ReferencePair pair1 = new ReferencePair("aa","bb");
        ReferencePair pair2 = new ReferencePair("aa","bb");

        HashMap<String,String> pairMap = new HashMap<>();

        pairMap.put("aa","bb");

//        System.out.println(pair1.equals(pair2));

        HashMap<HashMap<String,String>,String> map = new HashMap<>();
        map.put(pairMap,"BWEEE");
//        map.put(pair2,"BWEEE");
//
//        System.out.println(map.get(pair1));

        System.out.println(map.get(pairMap));
    }
}
