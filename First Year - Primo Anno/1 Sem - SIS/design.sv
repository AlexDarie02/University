
module MorraCinese(
  input [1:0] primo, [1:0] secondo, [0:0]in, [0:0]clk, 
  output reg [1:0] manche, [1:0] partita
  );
  
  reg [1:0] pp, ps;
  reg [0:0] mm, mf;
  reg [4:0] totale, cmp;
  reg [2:0] stato, nextStato;
  
  initial begin
    pp = 2'b00;
    ps = 2'b00;
    totale = 5'b00000;
    cmp = 5'b00000;
    stato = 3'b000;
    mm = 1'b0;
    mf = 1'b0;
  end
  
  always @ (posedge clk) begin : AGGIORNA
    stato = nextStato;
  end
  
  always @(posedge clk) begin : DP
    if (in == 1'b1) begin
        pp = 2'b00;
        ps = 2'b00;
        cmp = 5'b00000;
        stato = 3'b000;
        totale = 5'b00100+primo*3'b100+secondo;
        manche = 2'b00;
    end
    else if (pp == primo || ps == secondo || primo == 2'b00 || secondo == 2'b00) begin
        manche = 2'b00;
    end
    else if (primo == secondo) begin
        manche = 2'b11;
    end
    else if (primo == 2'b01) begin
        if (secondo == 2'b10) begin
            manche = 2'b10;
        end
        else begin
            manche = 2'b01;
        end
    end
    else if (primo == 2'b10) begin
        if (secondo == 2'b01) begin
            manche = 2'b01;
        end
        else begin
            manche = 2'b10;
        end
    end
    else if (primo == 2'b11) begin
        if (secondo == 2'b01) begin
            manche = 2'b10;
        end
        else begin
            manche = 2'b01;
        end
    end
    
    if (manche != 2'b00) begin
        cmp = cmp + 1;
    end
    
    if (cmp == totale && in == 1'b0) begin
        mf = 1'b1;
    end else begin
        mf = 1'b0;
    end
    
    if (cmp > 3'b011) begin
        mm = 1'b1;
    end else begin
        mm = 1'b0;
    end
    
    if (manche == 2'b01) begin
        pp = primo;
        ps = 2'b00;
    end
    else if (manche == 2'b10) begin
        pp = 2'b00;
        ps = secondo;
    end
    else if (manche == 2'b11) begin
        pp = 2'b00;
        ps = 2'b00;
    end
    $display("manche: %b, mm: %b, mf: %b, next: %b, partita: %b", manche, mm, mf, nextStato, partita);
end

  always @ (stato, manche, in) begin : FSM
    if (in == 1'b1) begin
      nextStato = 3'b000;
      partita = 2'b00;
    end
  else begin
    case (stato)
    //START
      3'b000: begin
        if (manche == 2'b01 && mf == 1'b0) begin
          nextStato = 3'b001;
          partita = 2'b00;
        end
        else if (manche == 2'b10 && mf == 1'b0) begin
          nextStato = 3'b100;
          partita = 2'b00;
        end
        else if (mf == 1'b1) begin
          nextStato = 3'b111;
          partita = 2'b11;
        end
        else begin
          nextStato = 3'b000;
          partita = 2'b00;
        end
        end

    //P1
      3'b001: begin
        if(manche == 2'b01 && mm == 1'b0) begin
            nextStato = 3'b011;
            partita=2'b00;
        end
        else if (manche == 2'b11 && mf == 1'b0) begin
            nextStato = 3'b001;
            partita = 2'b00;
        end
        else if (manche == 2'b10 && mf == 1'b0) begin
            nextStato = 3'b000;
            partita = 2'b00;
        end
        else if (manche == 2'b10 && mf == 1'b1) begin
            nextStato = 3'b111;
            partita = 2'b11;
        end
        else if (manche == 2'b11 && mf == 1'b1) begin 
            nextStato = 3'b111;
            partita = 2'b11;
        end
        else if (manche == 2'b01 && mm == 1'b1) begin
            nextStato = 3'b010;
            partita = 2'b01;
        end
        else begin
            nextStato = 3'b001;
            partita = 2'b00;
        end
        end

    //P2
     3'b011: begin
        if (manche == 2'b10 && mf == 1'b0) begin 
            nextStato = 3'b001;
            partita = 2'b00;
        end
        else if (manche == 2'b11 && mm == 1'b1) begin 
            nextStato = 3'b010;
            partita = 2'b01;
        end
        else if (manche == 2'b01 && mm == 1'b0) begin
            nextStato = 3'b010;
            partita = 2'b00;
        end
        else if (manche == 2'b01 && mm == 1'b1) begin 
            nextStato = 3'b010;
            partita = 2'b01;
        end
        else if (manche == 2'b11 && mm == 1'b0) begin
            nextStato = 3'b011;
            partita = 2'b00;
        end
        else if (manche == 2'b00) begin
            nextStato = 3'b011;
            partita = 2'b00;
        end
        else begin
            nextStato = 3'b111;
            partita = 2'b11;
        end
     end

    //PV
     3'b010: begin
        if(manche == 2'b00) begin
            nextStato = 3'b010;
            partita = 2'b00;
        end
        else begin
            nextStato = 3'b010;
            partita = 2'b01;
        end
        end

    //S1
     3'b100: begin
        
        if (manche == 2'b01 && mf == 1'b0) begin
            nextStato = 3'b000;
            partita = 2'b00;
        end
        else if (manche == 2'b11 && mf == 1'b1) begin
            nextStato = 3'b111;
            partita = 2'b11;
        end
        else if (manche == 2'b01 && mf == 1'b1) begin
            nextStato = 3'b111;
            partita = 2'b11;
        end
        else if (manche == 2'b10 && mm == 1'b1) begin
            nextStato = 3'b101;
            partita = 2'b10;
        end
        else if (manche == 2'b10 && mm == 1'b0) begin
            nextStato = 3'b110;
            partita = 2'b00;
        end
        else if (manche == 2'b00) begin
            nextStato = 3'b100;
            partita = 2'b00;
        end
        else if(manche == 2'b11 && mf == 1'b0) begin
            nextStato = 3'b100;
            partita = 2'b00;
        end
        else begin
            nextStato = 3'b100;
            partita = 2'b00;
        end
        end

    //S2
     3'b110: begin
        if (manche == 2'b01 && mf == 1'b0) begin
            nextStato = 3'b100;
            partita = 2'b00;
        end
        else if (manche == 2'b11 && mm == 1'b0) begin
            nextStato = 3'b110;
            partita = 2'b00;
        end
        else if (manche == 2'b00) begin
            nextStato = 3'b110;
            partita = 2'b00;
        end
        else if (manche == 2'b01 && mf == 1'b1) begin
            nextStato = 3'b111;
            partita = 2'b11;
        end
        else if (manche == 2'b10 && mm == 1'b0) begin
            nextStato = 3'b101;
            partita = 2'b00;
        end
        else if (manche == 2'b10 && mm == 1'b1) begin
            nextStato = 3'b101;
            partita = 2'b10;
        end
        else begin
            nextStato = 3'b101;
            partita = 2'b10;
        end
        end

    //SV
     3'b101: begin
        if(manche == 2'b00) begin
            nextStato = 3'b101;
            partita = 2'b00;
        end
        else begin
            nextStato = 3'b101;
            partita = 2'b10;
        end
        end
  

    //PAR
     3'b111: begin
        nextStato = 3'b111;
        partita = 2'b11;
        end

    endcase
   end
  end
 endmodule
  