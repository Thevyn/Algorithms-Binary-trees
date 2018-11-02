////////////////// ObligSBinTre /////////////////////////////////

import java.util.*;

public class ObligSBinTre<T> implements Beholder<T>
{
    private static final class Node<T>   // en indre nodeklasse
    {
        private T verdi;                   // nodens verdi
        private Node<T> venstre, høyre;    // venstre og høyre barn
        private Node<T> forelder;          // forelder

        // konstruktør
        private Node(T verdi, Node<T> v, Node<T> h, Node<T> forelder)
        {
            this.verdi = verdi;
            venstre = v; høyre = h;
            this.forelder = forelder;
        }

        private Node(T verdi, Node<T> forelder)  // konstruktør
        {
            this(verdi, null, null, forelder);
        }

        @Override
        public String toString(){ return "" + verdi;}

    } // class Node

    private Node<T> rot;                            // peker til rotnoden
    private int antall;                             // antall noder
    private int endringer;                          // antall endringer

    private final Comparator<? super T> comp;       // komparator

    public ObligSBinTre(Comparator<? super T> c)    // konstruktør
    {
        rot = null;
        antall = 0;
        comp = c;
    }

    @Override
    public boolean leggInn(T verdi)
    {
        Objects.requireNonNull(verdi, "Ulovlig med nullverdier!");

        Node<T> p = rot, q = null;               // p starter i roten
        int cmp = 0;                             // hjelpevariabel

        while (p != null) {       // fortsetter til p er ute av treet
            q = p;                                 // q er forelder til p
            cmp = comp.compare(verdi,p.verdi);     // bruker komparatoren
            p = cmp < 0 ? p.venstre : p.høyre;     // flytter p
        }

        // p er nå null, dvs. ute av treet, q er den siste vi passerte

        p = new Node<>(verdi, q);                   // oppretter en ny node

        if (q == null) {
            rot = p;                  // p blir rotnode
        } else if (cmp < 0) {
            q.venstre = p;         // venstre barn til q
        } else {
            q.høyre = p;                        // høyre barn til q
        }

        antall++;                                // én verdi mer i treet
        return true;                             // vellykket innlegging
    }

    @Override
    public boolean inneholder(T verdi)
    {
        if (verdi == null) return false;

        Node<T> p = rot;

        while (p != null)
        {
            int cmp = comp.compare(verdi, p.verdi);
            if (cmp < 0) p = p.venstre;
            else if (cmp > 0) p = p.høyre;
            else return true;
        }

        return false;
    }

    // kode fra kapittel 5.2 8 D i laerebok, med endringer i pekere
    @Override
    public boolean fjern(T verdi)
    {
        if (verdi == null) return false;  // treet har ingen nullverdier

        Node<T> p = rot, q = null;   // q skal være forelder til p

        while (p != null)            // leter etter verdi
        {
            int cmp = comp.compare(verdi,p.verdi);      // sammenligner
            if (cmp < 0) {
                q = p;
                p = p.venstre;
            }      // går til venstre
            else if (cmp > 0) {
                q = p;
                p = p.høyre;
            }   // går til høyre
            else break;    // den søkte verdien ligger i p
        }
        if (p == null) {
            return false;   // finner ikke verdi
        }

        if (p.venstre == null || p.høyre == null)  // Tilfelle 1) og 2)
        {
            Node<T> b;
            if (p.venstre != null) {
                b = p.venstre;
            } else {
                b = p.høyre;
            }
            if (p == rot) {
                rot = b;
            } else if (p == q.venstre) {
                q.venstre = b;
                if (b != null) {
                    b.forelder = q;
                }
            } else {
                q.høyre = b;
                if (b != null) {
                    b.forelder = q;
                }
            }
        }
        else  // Tilfelle 3)
        {
            Node<T> s = p, r = p.høyre;   // finner neste i inorden
            while (r.venstre != null)
            {
                // s = r;    // s er forelder til r
                r = r.venstre;
            }

            p.verdi = r.verdi;   // kopierer verdien i r til p
            s = r.forelder;

            if (s != p) {
                s.venstre = r.høyre;
                if (r.høyre != null) {
                    r.høyre.forelder = s.venstre;
                }
            } else {
                s.høyre = r.høyre;
                if (r.høyre != null) {
                    r.høyre.forelder = s.høyre;
                }
            }
        }

        endringer++; // vi har gjort en endring
        antall--;   // det er nå én node mindre i treet
        return true;
    }

    public int fjernAlle(T verdi)
    {
        int antallSlettet = 0;

        // det passer fint at fjern() er en boolean,
        // da kan vi bare bruke en while lokke og kalle
        // paa den for aa fjerne alle verdier.
        while (fjern(verdi)) {
            antallSlettet++;
        }
        return antallSlettet;
    }

    @Override
    public int antall()
    {
        return antall;
    }

    public int antall(T verdi)
    {
        Node<T> p = rot;
        int antallVerdi = 0;

        while (p != null)
        {
            int cmp = comp.compare(verdi, p.verdi);
            if (cmp < 0) p = p.venstre;
            else
            {
                if (cmp == 0) antallVerdi++;
                p = p.høyre;
            }
        }
        return antallVerdi;
    }

    @Override
    public boolean tom()
    {
        return antall == 0;
    }


    // rekursiv hjelpemetode til nullstill()
    public Node<T> post(Node p) {
        if (p == null) {
            return null;
        }
        p.venstre = post(p.venstre);
        p.høyre = post(p.høyre);
        p = null;
        antall--;
        endringer++;
        return p;
    }

    @Override
    public void nullstill()
    {
        if (tom()) {
            return;
        }
        // kaller paa post for aa rekursivt slette noder
        post(rot);

    }

    /*
        nesteInorden bygger paa kode fra laerebok kap 5.1
     */
    private static <T> Node<T> nesteInorden(Node<T> p)
    {
        Objects.requireNonNull(p);

        if (p.høyre != null) {
            p = p.høyre;
            while (p.venstre != null) {
                p = p.venstre;
            }
        } else {
            while (p.forelder != null && p == p.forelder.høyre) {
                p = p.forelder;
            }
            p = p.forelder;
        }
        return p;

    }

    /*
        tar i bruk nesteInorden, og bruker en while lokke
        som oppgavetekst foreslaar. Deler av koden er den
        samme jeg brukte i oblig2
     */
    @Override
    public String toString()
    {
        if (antall < 1){
            return "[]";
        }
        Node p = rot;

        while(p.venstre != null){
            p = p.venstre;
        }

        StringBuilder text = new StringBuilder();
        text.append("[").append(p.verdi);

        p = nesteInorden(p);

        int teller = 0;
        while (teller < antall-1) {
            text.append(", ").append(p.verdi);
            p = nesteInorden(p);
            teller++;
        }

        text.append("]");
        return text.toString();
    }

    public String omvendtString() {

        if (tom()) {
            return "[]";
        }
        StringJoiner sj = new StringJoiner(", ", "[" , "]");

        ArrayDeque<Node<T>> stakk = new ArrayDeque<>();
        Node<T> p = rot;   // starter i roten og går til venstre

        for (; p.høyre != null; p = p.høyre) stakk.addLast(p); // Legger til sist på stakken
        sj.add(p.verdi.toString()); // Legger inn første verdi

        while (true) {
            if (p.venstre != null)          // til høyre i venstre subtre
            {

                for (p = p.venstre; p.høyre != null; p = p.høyre) {
                    stakk.add(p);

                }
            }   else if (!stakk.isEmpty()) {
                p = stakk.removeLast();   // p.høyre == null, henter fra stakken
            }
            else break;          // stakken er tom - vi er ferdig

            sj.add(p.verdi.toString());
        } // while



        return sj.toString();


    }


    public String høyreGren()
    {
        if(rot == null) return "[]";

        StringBuilder s = new StringBuilder();

        s.append( "[" );

        Node<T> p = rot;


        if (p.høyre == null && p.venstre == null) {            // Sjekker om første gren er en bladnode
            s.append(p.verdi);
        }

        else {
            s.append(p.verdi);
            s.append(", ");

            while (p.høyre != null || p.venstre != null) {     // Slutter ved en bladnode

                if (p.høyre != null) {                         // Går til høyre om noden har et høyrebarn

                    p = p.høyre;
                    s.append(p.verdi);
                }

                else {                                           // Går til venstre hvis ikke noden har et høyrebarn

                    p = p.venstre;
                    s.append(p.verdi);
                }

                if (p.høyre == null && p.venstre == null) break; // Sjekker om noden er sist i treet

                else s.append(", ");
            }
        }

        s.append( "]" );
        return s.toString();
    }

    public String lengstGren()
    {
        if (rot == null) return "[]";

        Deque<Node> deque = new ArrayDeque<>();
        deque.add(rot);

        Node<T> p = null;

        while (!deque.isEmpty())
        {
            p = deque.remove();

            if (p.høyre != null) deque.add(p.høyre);

            if (p.venstre != null) deque.add(p.venstre);

        }

        List<T> liste = new ArrayList<>();

        Node<T> q = rot;

        while (q != null)
        {
            liste.add(q.verdi);
            if (comp.compare(p.verdi, q.verdi) < 0) {
                q = q.venstre;
            }
            else {
                q = q.høyre;
            }
        }

        return liste.toString();
    }

    public String[] grener() {
        if (tom()) {
            return new String[0];
        }

        ArrayDeque<T> stakk = new ArrayDeque<>();
        ArrayList<String> tabelliste = new ArrayList<>();
        Node p = rot;
        grener(p,tabelliste, stakk);

        String[] tabell = new String[tabelliste.size()];
        for (int i = 0; i < tabell.length; i++) {
            tabell[i] = tabelliste.get(i);
        }

        return tabell;

    }

    private void grener(Node<T> p, ArrayList<String> tabellliste, ArrayDeque<T> stakk) {

        stakk.addLast(p.verdi);
        if (p.venstre != null) grener(p.venstre, tabellliste, stakk);
        if (p.høyre != null) grener(p.høyre, tabellliste, stakk);
        if (p.venstre == null && p.høyre == null) tabellliste.add(stakk.toString());

        stakk.removeLast();
    }

     public String printblader(Node<T> rot,StringJoiner s){

        if(rot == null){
            return s.toString();        //hvis rot == null så returnerer den en string
        }
        if(rot.venstre !=null){
            printblader(rot.venstre,s);   //så lenge venstre ikke er null, så sjekker metoden venstre side av treet
        }
        if(rot.høyre !=null){
            printblader(rot.høyre,s);   //så lenge høyre ikke er null, så sjekker metoden høyre side av treet
        }
        if(rot.venstre == null && rot.høyre == null) {

            s.add(rot.verdi.toString());        //legger til verdien inn i en stringoiner
//        }
        }
            return s.toString();

    }

    public String bladnodeverdier()
    {
        //throw new UnsupportedOperationException("Ikke kodet ennå!");
//     return printblader(rot);
        StringJoiner s = new StringJoiner(", ", "[", "]");

        if(!tom()){
            printblader(rot,s);
        }

        return s.toString();
    }

    public String postString(){
        java.util.ArrayDeque<Node<T>> stakk = new java.util.ArrayDeque<Node<T>>();
        //StringJoiner s = new StringJoiner(", ");
        if(rot == null){
                    return "[]";
                }
            Node<T> p = rot;
            stakk.addFirst(p);  // legger til rot i stakk

        java.util.Deque<String> output = new java.util.ArrayDeque<>();

        while(!stakk.isEmpty()){

            int antall = stakk.size();

            for(int i = 0; i < antall; i++) {

                Node<T> current = stakk.removeFirst();
                output.addFirst(current.verdi.toString());

                if (current.venstre != null) {
                    stakk.addFirst(current.venstre);
                }

                if (current.høyre != null) {
                    stakk.addFirst(current.høyre);
                }
            }

        }

        return output.toString();

    }

    @Override
    public Iterator<T> iterator()
    {
        return new BladnodeIterator();
    }

    private class BladnodeIterator implements Iterator<T>
    {
        private Node<T> p = rot, q = null;
        private boolean removeOK = false;
        private int iteratorendringer = endringer;
  private BladnodeIterator()  // konstruktør
        {
            if (tom()) {
                return;
            }
                p = firstLeaf(rot);
                q = null;
                removeOK = false;
                iteratorendringer = endringer;

           }

           private Node<T> nestLeaf(Node<T> p){
            Node <T> a = p.forelder;
            while(a != null && (p ==a.venstre || p.høyre == null)){
                p = a;
                a = a.forelder;
            }
            return a == null ? null :firstLeaf(a.høyre);
           }

           private <T>Node<T> firstLeaf(Node <T> p){
            while(true){
                if(p.venstre != null){
                    p = p.venstre;
                }
                if(p.høyre !=null){
                    p = p.høyre;
                }else return p;
            }
           }

        @Override
        public boolean hasNext()
        {
            return p != null;  // Denne skal ikke endres!
        }

        @Override
      
        public T next()
        {
           // throw new UnsupportedOperationException("Ikke kodet ennå!");
            if(!hasNext()){
                throw new NoSuchElementException("Treet har ikke flere bladnoder");
            }
	if(iteratorendringer != endringer){
            throw new ConcurrentModificationException("iteratorendringer er " + iteratorendringer + " og" + endringer);
           removeOK = true;
            q = p;
	p = p.nestLeaf(p);
	return q.verdi;
           
	   
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("Ikke kodet ennå!");
        }

    } // BladnodeIterator

} // ObligSBinTre