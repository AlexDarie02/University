
`timescale 1ns / 1ps

module tb_MorraCinese();
  integer tbf, outf;
  reg clk, inizia; 
  reg [1:0] primo, secondo;
  wire [1:0] manche, partita;
  
  MorraCinese mc(primo, secondo, inizia, clk, manche, partita);
  
  always #10 clk = ~clk;
  
  initial begin
    $dumpfile("dump.vcd");
    $dumpvars;

    tbf = $fopen("testbench.script", "w");
    outf = $fopen("output_verilog.txt", "w");

    $fdisplay(tbf, "read_blif FSMD.blif");
  	
    clk = 1'b0;


  // Partita 1
  inizia = 1'b1;
  primo = 2'b00; secondo = 2'b00; // max 4 manche
  $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
  #20
  $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);

    // Manche 1 : carta vs forbice
    inizia = 1'b0;
    primo = 2'b10; secondo = 2'b11; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // ha vinto il secondo
    // vantaggio 1

    // Manche 2: forbice vs carta
    primo = 2'b11; secondo = 2'b10; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // ha vinto il primo
    // vantaggio 0

    // Manche 3: forbice vs sasso
    primo = 2'b11; secondo = 2'b01; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // Manche non valida quindi non conteggiata, primo ha messo mossa proibita
    // vantaggio 0

    // Manche 3: forbice vs carta
    primo = 2'b11; secondo = 2'b10; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // Manche non valida quindi non conteggiata, primo ha messo AGAIN la mossa proibita
    // vantaggio 0

    // Manche 3: carta vs sasso
    primo = 2'b10; secondo = 2'b01; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // ha vinto il primo
    // vantaggio di 1

    // Manche 4: sasso vs carta
    primo = 2'b01; secondo = 2'b10; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // ha vinto il secondo
    // vantaggio di 0

  // Partita 1 terminata in pareggio 


  // Partita 2
  inizia = 1'b1;
  primo = 2'b00; secondo = 2'b10; // max 6 manche
  $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
  #20
  $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
  
    // Manche 1: mossa non valida vs sasso
    inizia = 1'b0;
    primo = 2'b00; secondo = 2'b01; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // Manche non valida quindi non conteggiata
    // vantaggio di 0

    // Manche 1: sasso vs forbice
    primo = 2'b01; secondo = 2'b11; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // ha vinto il primo
    // vantaggio di 1

    // Manche 2: forbice vs forbice
    primo = 2'b11; secondo = 2'b11; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // pareggio
    // vantaggio di 1

    // Manche 3: sasso vs carta
    primo = 2'b01; secondo = 2'b10; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // vince il secondo 
    // vantaggio di 0

    // Manche 4: carta vs forbice
    primo = 2'b10; secondo = 2'b11; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // vince il secondo
    // vantaggio di 1

    // Manche 5: carta vs carta
    primo = 2'b10; secondo = 2'b10; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // pareggio
    // vantaggio di 1

    // Manche 6: sasso vs forbice
    primo = 2'b01; secondo = 2'b11; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // ha vinto il primo
    // vantaggio di 0

  // Partita 2 terminata in pareggio 


  // Partita 3
  inizia = 1'b1;
  primo = 2'b11; secondo = 2'b11; // max 19 manche
  $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
  #20
  $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);

    // Manche 1: carta vs sasso
    inizia = 1'b0;
    primo = 2'b10; secondo = 2'b01; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // ha vinto il primo
    // vantaggio di 1

    // Manche 2: carta vs forbice
    primo = 2'b10; secondo = 2'b01; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // Manche non valida quindi non conteggiata, primo ha inserito la mossa proibita
    // vantaggio di 1

    // Manche 3: forbice vs carta
    primo = 2'b11; secondo = 2'b10; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // ha vinto il primo
    // vantaggio di 2, ma non si decreta ancora vittoria bc devono essere giocate almeno 4 manches

    // Manche 4: sasso vs carta
    primo = 2'b01; secondo = 2'b10; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // ha vinto il secondo
    // vantaggio di 1

    // Manche 5: sasso vs forbice
    primo = 2'b01; secondo = 2'b11; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // ha vinto il primo
    // vantaggio di 2 e le manche minime finite, quindi si puo decretare vittoria

// Partita 3 vinta dal primo 


// Partita 4
inizia = 1'b1;
primo = 2'b01; secondo = 2'b00; // max 8 manche
$fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
#20
$fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);

    // Manche 1: forbice vs carta
    inizia = 1'b0;
    primo = 2'b11; secondo = 2'b10; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // ha vinto il primo
    // vantaggio di 1

    // Manche 2: sasso vs carta
    primo = 2'b01; secondo = 2'b10; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // ha vinto il secondo
    // vantaggio di 0

    // Manche 3: carta vs carta
    primo = 2'b10; secondo = 2'b10; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // Manche non valida quindi non conteggiata, secondo ha messo la mossa proibita
    // vantaggio di 0

    // Manche 3:  sasso vs forbice
    primo = 2'b01; secondo = 2'b11; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // ha vinto il primo
    // vantaggio di 1

    // Manche 4: carta vs carta
    primo = 2'b10; secondo = 2'b10; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // pareggio
    // vantaggio di 1

    // Manche 5: carta vs sasso
    primo = 2'b10; secondo = 2'b01; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // ha vinto il primo
    // vantaggio di 2

  // Partita 4 vinta dal primo 

  
  // Partita 5
  inizia = 1'b1;
  primo = 2'b10; secondo = 2'b10; // max 14 manche
  $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
  #20
  $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);

    // Manche 1: sasso vs sasso
    inizia = 1'b0;
    primo = 2'b01; secondo = 2'b01; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // pareggio
    // vantaggio di 0

    // Manche 2: sasso vs carta
    primo = 2'b01; secondo = 2'b10; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // ha vinto il secondo
    // vantaggio di 1

    // Manche 3: forbice vs sasso
    primo = 2'b11; secondo = 2'b01; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // ha vinto il secondo
    // vantaggio di 2

    // Manche 4: carta vs sasso
    primo = 2'b10; secondo = 2'b01; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // Manche non valida quindi non conteggiata, secondo ha inserito la mossa proibita
    // vantaggio di 2

    // Manche 4: forbice vs forbice
    primo = 2'b11; secondo = 2'b11; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // pareggio
    // vantaggio di 2

  // Partita 5 vinta dal secondo 


  // Partita 6
  inizia = 1'b1;
  primo = 2'b01; secondo = 2'b10; // max 10 manche
  $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
  #20
  $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);

    // Manche 1: forbice vs sasso
    inizia = 1'b0;
    primo = 2'b11; secondo = 2'b01; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // ha vinto il secondo
    // vantaggio di 1

    // Manche 2: sasso vs carta
    primo = 2'b01; secondo = 2'b10; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // ha vinto il secondo
    // vantaggio di 2

    // Manche 3: carta vs forbice
    primo = 2'b10; secondo = 2'b11; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // ha vinto il secondo
    // vantaggio di 3

    // Manche 4: forbice vs carta
    primo = 2'b11; secondo = 2'b10; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // ha vinto il primo
    // vantaggio di 2
  // Partita 6 vinta dal secondo 


  // Partita 7
  inizia = 1'b1;
  primo = 2'b01; secondo = 2'b01; // max 9 manche
  $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
  #20
  $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);

    // Manche 1: carta vs sasso
    inizia = 1'b0;
    primo = 2'b10; secondo = 2'b01; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // ha vinto il primo
    // vantaggio di 1

    // Manche 2: forbice vs mossa non valida 
    primo = 2'b11; secondo = 2'b00; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // Manche non valida quindi non conteggiata
    // vantaggio di 1

    // Manche 2: carta vs forbice
    primo = 2'b10; secondo = 2'b11; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // Manche non valida quindi non conteggiata, primo ha inserito la mossa proibita
    // vantaggio di 1

    // Manche 2: sasso vs forbice
    primo = 2'b01; secondo = 2'b11; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // ha vinto il primo
    // vantaggio di 2

    // Manche 3: forbice vs carta
    primo = 2'b11; secondo = 2'b10; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // ha vinto il primo
    // vantaggio di 3

    // Manche 4: sasso vs forbice 
    primo = 2'b01; secondo = 2'b11; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // ha vinto il primo
    // vantaggio di 4

  // Partita 7 vinta dal primo 


  // Partita 8
  inizia = 1'b1;
  primo = 2'b00; secondo = 2'b00; // max 4 manche
  $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
  #20
  $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);

    // Manche 1 : forbice vs sasso
    inizia = 1'b0;
    primo = 2'b11; secondo = 2'b01; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // ha vinto il secondo
    // vantaggio 1

    // Manche 2: sasso vs forbice
    primo = 2'b01; secondo = 2'b11; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // ha vinto il primo
    // vantaggio 0

    // Manche 3: carta vs carta
    primo = 2'b10; secondo = 2'b10; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // pareggio
    // vantaggio 0

    // Manche 4: sasso vs carta
    primo = 2'b01; secondo = 2'b10; 
    $fdisplay(tbf, "simulate %b %b %b %b %b", primo[1], primo[0], secondo[1], secondo[0], inizia);
    #20
    $fdisplay(outf, "Outputs: %b %b %b %b", manche[1], manche[0], partita[1], partita[0]);
    // ha vinto il secondo
    // vantaggio 1

  // Partita 8 terminata in pareggio  


    $fdisplay(tbf, "quit");

    $fclose(tbf);
    $fclose(outf);
    $finish;
  end 
  
endmodule