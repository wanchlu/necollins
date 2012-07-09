#!/usr/bin/perl

use strict;

my %feathash;
my $maxfeat = 0;

my $trainFile = shift;
my $trainsize = shift;
my $testFile = shift;

`./shuffle.pl $trainFile | head -$trainsize > $trainFile.shuffle`;

open (TRAIN, "$trainFile.shuffle");
open (TRAINLABELS, ">$trainFile.labels");
open (NEWTRAIN, ">$trainFile.tumbl");

my $count = 0;

while (<TRAIN>) {
  chomp;
  $count++;
  my @feats = split (/\s+/);
  print TRAINLABELS $feats[0], "\n";
  foreach my $f (1..$#feats) {
    print NEWTRAIN hashvalue($feats[$f]), "\t", $count, "\t1\n";
  }
}

close TRAIN;
close TRAINLABELS;
close NEWTRAIN;

open (TEST, $testFile);
open (TESTLABELS, ">$testFile.labels");
open (NEWTEST, ">$testFile.tumbl");

my $count = 0;

while (<TEST>) {
  chomp;
  $count++;
  my @feats = split (/\s+/);
  print TESTLABELS $feats[0], "\n";
  foreach my $f (1..$#feats) {
    print NEWTEST hashvalue($feats[$f]), "\t", $count, "\t1\n";
  }
}

close TEST;
close TESTLABELS;
close NEWTEST;


sub hashvalue {
  my $f = shift;
  if (exists $feathash{$f}) {
    return $feathash{$f};
  }
  else {
    $feathash{$f} = ++$maxfeat;
    return $maxfeat;
  }
}
