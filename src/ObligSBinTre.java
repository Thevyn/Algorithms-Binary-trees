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

    @Override
    public boolean fjern(T verdi)
    {
        throw new UnsupportedOperationException("Ikke kodet ennå!");
    }

    public int fjernAlle(T verdi)
    {
        throw new UnsupportedOperationException("Ikke kodet ennå!");
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



    @Override
    public void nullstill()
    {
        throw new UnsupportedOperationException("Ikke kodet ennå!");
    }

    private static <T> Node<T> nesteInorden(Node<T> p)
    {
        throw new UnsupportedOperationException("Ikke kodet ennå!");
    }

    @Override
    public String toString()
    {
        throw new UnsupportedOperationException("Ikke kodet ennå!");
    }

    public String omvendtString() {
        StringBuilder s = new StringBuilder();
        s.append("[");

        if (tom()) {
            return "[]";
        }
            ArrayDeque<Node<T>> stakk = new ArrayDeque<>();
            Node<T> p = rot;   // starter i roten og går til venstre

            for (; p.høyre != null; p = p.høyre) stakk.push(p);

            while (true) {
                s.append(p.verdi).append(",").append(" ");

                if (p.venstre != null)          // til venstre i høyre subtre
                {
                    for (p = p.venstre; p.høyre != null; p = p.høyre) {
                        stakk.push(p);

                    }
                }   else if (!stakk.isEmpty()) {
                    p = stakk.pop();   // p.høyre == null, henter fra stakken
                }
                else break;          // stakken er tom - vi er ferdig

            } // while


            s.append("]");

        return s.toString();
    }


    public String høyreGren()
    {
        throw new UnsupportedOperationException("Ikke kodet ennå!");
    }

    public String lengstGren()
    {
        throw new UnsupportedOperationException("Ikke kodet ennå!");
    }

    public String[] grener() {
        if (tom()) {
            return new String[]{};
        }

        Deque<Node<T>> stakk = new ArrayDeque<>();
        String[] tabell;
        Node p = rot;

        for (; p.høyre != null; p = p.høyre) stakk.push(p);



    }

     public String printblader(Node<T> rot){
           // Node<T> p = rot;
        StringBuilder print = new StringBuilder();
       // StringJoiner s = new StringJoiner(",", "[", "]");

        //legger inn den første verdien
        print.append("[");

        if (rot == null){
            return "[]";
        }
        if(rot.venstre == null && rot.høyre == null){
            //  System.out.print(rot.verdi+" ");,
            print.append(rot.verdi);
        }
        printblader(rot.venstre);
        printblader(rot.høyre);

//
        print.append("]");
        return print.toString();

    }

    public String bladnodeverdier()
    {
        //throw new UnsupportedOperationException("Ikke kodet ennå!");
//     return printblader(rot);
     return printblader(rot);
    }

    public String postString(){
        java.util.ArrayDeque<Node<T>> queue = new java.util.ArrayDeque<Node<T>>();
            Node<T> p = rot;
        queue.addFirst(p);
        StringJoiner s = new StringJoiner(",", "[", "]");

        while(!queue.isEmpty()){

            int antall = queue.size();

            for(int i = 0; i < antall; i++){
                Node<T> current  = queue.removeLast();

                s.add(current.verdi.toString());

                if(current.høyre !=null){
                    queue.addFirst(current.høyre);
                }

                if(current.venstre != null){
                    queue.addFirst(current.venstre);
                }



            }
        }
       // s.append("]");

        return s.toString();

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
            throw new UnsupportedOperationException("Ikke kodet ennå!");
        }

        @Override
        public boolean hasNext()
        {
            return p != null;  // Denne skal ikke endres!
        }

        @Override
        public T next()
        {
            throw new UnsupportedOperationException("Ikke kodet ennå!");
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("Ikke kodet ennå!");
        }

    } // BladnodeIterator

} // ObligSBinTre
