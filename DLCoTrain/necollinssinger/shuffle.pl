#!/usr/bin/perl -w
use strict;

my @ordered = <>;

srand;
my @shuffled = ();
while (@ordered) {
    push @shuffled, splice(@ordered, rand @ordered, 1);
}

foreach my $line (@shuffled) {
    print $line unless $line =~ /^\s*$/;
}
