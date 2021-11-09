package com.company;

import com.sun.net.httpserver.Authenticator;

import java.util.ArrayList;
import java.util.Random;
import java.util.*;

class Chromosome implements Comparable<Chromosome>{  // Kromozom nesnelerinin oluşturulacağı kromozom sınıfı

    int passcode_length = 19;
    int population_size = 16;
    int matches;
    int elite_size =2;
    String[] chromosome; //oluşturulacak olan kromozomun tutulacağı array

    Random rand = new Random();

    public Chromosome(int matches,String[] chromosome){ // constructor metot

        this.matches=matches;
        this.chromosome=chromosome;


    }
    public int getMatches(){
        return matches;

    }
    public String[] getChromosome(){
        return chromosome;

    }

    // rastgele üretilen sayı ve rastgele seçilen harflere göre kromozomlar oluşturan ve oluşturulan kromozomları populasyona ekleyen metot

    public ArrayList<String[]> create_chromosome(int passcode_length,int population_size){

        ArrayList<String[]> population = new ArrayList<>();
        for( int i=0; i<population_size;i++){

            String[] chromosome =new String[passcode_length];
            for(int x=0; x<passcode_length;x++){
                String chars = "abcçdefgğhıijklmnoöprsştuüvYyZz";
                Random rnd = new Random();
                char c = chars.charAt(rnd.nextInt(chars.length()));

                chromosome[x]=String.valueOf(c);// char dizisinden seçilen rastgele harfleri kullanarak kromozom oluşturuluyor


            }
            Chromosome chromosomeObj = new Chromosome(0,chromosome); // kromozom nesnesi yaratılıyor
            population.add(chromosome); //  Array olarak da tanımlayabiliriz population[i]=chromosome
            //oluşturulan kromozom populasyona ekleniyor
        }

        return population; // oluşturulan populasyon geriye döndürülüyor
    }

    public ArrayList<Chromosome> fitness(ArrayList<String[]> population, String[] secret_passcode,int passcode_length){ // fitness score'u (yani şifre ile oluşturulan kromozomları karşılaştırarak eşleşme sayısını)hesaplayan metot

        ArrayList<Chromosome> fitness_scores = new ArrayList<>();

        for( String[] chromosome: population){
            Chromosome result = new Chromosome(0,chromosome); //populationin içindeki herbir chromosome listesini alarak Chromosome objesi oluşturuyor


            for(int index=0;index< passcode_length;index++){
                if(secret_passcode[index].equals(chromosome[index])){ // secret_passcode ile oluşturulan kromozomlar karşılaştırılıyor ve buna göre eşleşme sayısı bulunuyor
                    matches += 1; // eşleşme sayısı döngü boyunca güncelleniyor

                }
            }
            result.matches=matches;

            fitness_scores.add(result); // bulunulan sonuç fitness_score arraylist'ine ekleniyor
        }


        return fitness_scores; // fitness_scorelar geriye döndürülüyor

    }

    @Override
    public int compareTo(Chromosome comparestu)
    {
        int compareage = ((Chromosome)comparestu).getMatches();//fitness_scores arrayListesini matches değerine göre sıralıyor

        return compareage-this.matches; //descendibg order

    }

    public ArrayList<Chromosome> parentsList(ArrayList<Chromosome> fitness_scores){
        ArrayList<Chromosome> parentsList = new ArrayList<>();


        Collections.sort(fitness_scores); // fitness_scorelar sıralanıyor

        for (Chromosome result : fitness_scores.subList(0,2) ) { //arrayListin sort edilmiş haldeki ilk beş elemanı parentliste ata

            parentsList.add(result);
        }
        return parentsList;
    }


    public Chromosome breed(Chromosome parent1, Chromosome parent2,ArrayList<Chromosome> parents ){

        String[] child= new String[passcode_length]; // yeni nesil için üretilecek olan çocuk kromozomu tutacak olan array
        Chromosome childObj = new Chromosome(0,child); // oluşturulan kromozom ve gereklibilgileri içerecek olan kromozom nesnesi

        parent1 = parents.get(0);
        parent2 = parents.get(1);


        int geneA = rand.nextInt(19); //0-19 aralığinda rastgele iki nokta seçiliyor ve seçilen bu noktalar crossing over uygulanacak parçalarin belirlenmesinde kullanılıyor
        int geneB = rand.nextInt(19);
        int startGene;
        int endGene;
        if(geneA>geneB){ //min ve max değerleri ayarlıyor. (crossing over için aradaki parçayı belirliyor)
            startGene = geneB;
            endGene = geneA;
        }
        else{
            startGene = geneA;
            endGene = geneB;
        }

        for(int i =0; i< passcode_length;i++){  //seçilen noktalar ve başlangıç_bitiş kısımları dikkate alınarak
                                               // parent1 ve parent2 den yeni çocuklar oluşurken crossing over gerçekleşiyor
            if( (i < startGene) || (i > endGene)){
                child[i]=parent1.chromosome[i];
            }

            else{
                child[i]=parent2.chromosome[i];
            }


        }
        return childObj; // parent1 ve parent2'den oluşturulan çocuk kromozom geriye döndürülüyor
    }

    public ArrayList<Chromosome> create_children(ArrayList<Chromosome> parentsList, ArrayList<String[]> population){
        ArrayList<Chromosome> children = new ArrayList<>(); // oluşturulan çocokların tutulacağı arraylist
        int num_new_children = population.size() - elite_size;

        for(int i=0;i<elite_size;i++){
            children.add(parentsList.get(i));
        }


        for(int i=0; i<num_new_children;i++){
            Chromosome parent1 = parentsList.get((int) (Math.random()* parentsList.size()));
            Chromosome parent2 = parentsList.get((int) (Math.random()* parentsList.size()));
            children.add(breed(parent1,parent2, parentsList)); // breed metodu çağırılarak oluşturulan her bir çocuk kromozom children arraylist'ine ekleniyor
        }
        return children; // yeni nesile ait çocuk kromozomların yer aldığı arraylist geriye döndürülüyor
    }

    public ArrayList<Chromosome> mutation(ArrayList<Chromosome> children){  // mutasyonun uygulandığı metot
        for( int i =0; i<children.size();i++){
            if(Math.random() > 0.1){
                continue;
            }

            else{
                String chars = "abcçdefgğhıijklmnoöprsştuüvYyZz";
                Random rnd = new Random();
                char c = chars.charAt(rnd.nextInt(chars.length()));

                // seçilen rastgele indexteki gene seçilen rastgele harf kullanılarak mutasyon uygulanıyor
                int mutated_position = (int) (Math.random()* passcode_length); // mutasyon uygulanacak pozisyon rastgele seçiliyor
                String mutation = String.valueOf(c); // mutasyon ile değişen harfin yerine gelecek olan yeni hrf rastgele seçiliyor
                children.get(i).chromosome[mutated_position] = mutation; // mutasyon uygulanıyor
            }

        }

        return children;
    }


}

public class Main {

    public static void main(String[] args) {
        String[] secret_passcode = {"Y", "a", "p", "a", "y", "Z", "e", "k", "a", "Y", "ö", "n", "t", "e", "m", "l", "e", "r", "i"};
        //String[] secret_passcode = {"Y","a","p","a","y"};
        //String[] secret_passcode = {"Y","a","p","a","y","Z","e","k","a"};

        int passcode_length = 19; // şifre uzunluğu belirtiliyor
        int population_size = 16;  // populasyon boyutu(populasyondaki kromozom sayısı) beliritliyor


        /***********************************************************************/
        int[] success = new int[population_size];
        int generations = 0;
        //t0 = time.time()

        int toplamGeneration=0;
        int döngüSay=5;
        for (int f = 0; f < döngüSay; f++) {

            long startTime = System.nanoTime();

            int[] bulunanGeneration = new int[döngüSay]; // kaçıncı generasyonda bulunduğunu tutacak olan dizi
            while (true) {

                String[] chromosome = new String[passcode_length];
                Chromosome x = new Chromosome(0, chromosome);


                ArrayList<String[]> population = new ArrayList<>();
                population = x.create_chromosome(passcode_length, population_size/*,chromosome*/);

                ArrayList<Chromosome> fitness_scores = new ArrayList<>();
                fitness_scores = x.fitness(population, secret_passcode, passcode_length);
                int found = 0;
                for (Chromosome matches : fitness_scores) {


                    if (matches.getMatches() == passcode_length) {
                        found++;
                        //System.out.println("Cracked in generations" + Integer.toString(generations));
                        bulunanGeneration[f] = generations;// kaçıncı generasyonda bulunduğunu array'e atıyoruz
                        toplamGeneration+=generations;
                        System.out.print("Discovered passcode!");
                        for (int i = 0; i < secret_passcode.length; i++) {
                            System.out.print(secret_passcode[i] + " ");
                        }
                        System.out.println();
                        break;
                    }

                }
                if (found == 1) { // 1 kez bulduysa bulduğu ilk generation'u görüntüleyecek
                    break;
                }

                ArrayList<Chromosome> parentsList = new ArrayList<>();
                parentsList = x.parentsList(fitness_scores);

                ArrayList<Chromosome> children = new ArrayList<>();
                children = x.create_children(parentsList, population);

                ArrayList<Chromosome> generation = new ArrayList<>();
                generation = x.mutation(children);

                generations += 1;
            }

            System.out.println("Cracked in generations" + Integer.toString(bulunanGeneration[f]));
            long stopTime = System.nanoTime();
            System.out.print("time: ");
            long elapsedTime = stopTime - startTime;
            double elapsedTimeInSecond = (double) elapsedTime / 1_000_000_000;

            System.out.println(elapsedTimeInSecond + " seconds");
            System.out.println("//////////////////////////////////////////////");

            population_size+=5; //populasyon boyutunun etkisini gözlemlemek için her turda populasyon boyutu 5 artırıldı
        }
        System.out.println("ORTALAMA GENERATİON SAYISI:"+toplamGeneration/döngüSay);


        }
    }

