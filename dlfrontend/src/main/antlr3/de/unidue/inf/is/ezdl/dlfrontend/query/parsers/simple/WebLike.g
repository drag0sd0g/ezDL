// ANTLR 3 grammar
//
// Possible queries are like these:
// term
// field=term
// field>=term
// (field=term AND otherfield<something) AND test OR <5
//
grammar WebLike;

options {output=AST;}


@parser::header { package de.unidue.inf.is.ezdl.dlfrontend.query.parsers.simple; }
@lexer::header { package de.unidue.inf.is.ezdl.dlfrontend.query.parsers.simple; }




query : subquery EOF;


subquery : andclause;


pseudoatomic: fieldexpression 
            | PAROP! (WS!)? subquery (WS!)? PARCLS! 
            | proximityclause;           


///////////////////////////////////////////////////////////////////////
//
// "Normal" operator priority
// Here, this means OR before AND, to make it easier to specify synonyms:
// syn1 OR syn2 OR syn3 AND term2 means (syn1 OR syn2 OR syn3) AND term2 
//

// 'AND'ing something either so "a b c" or so "a AND b AND c".
//
andclause: (a=orclause->$a) ( ( impland b+=orclause )+ -> ^(AND $a $b+) )? ; 

fragment impland: WS | (WS!)? AND (WS!)?;

fragment or: (WS!)? OR (WS!)?;


orclause: (a=pseudoatomic->$a) (( or b+=pseudoatomic )+ -> ^(OR $a $b+) )?; 



///////////////////////////////////////////////////////////////////////
//
// Proximity search
// Applies only to raw terms or phrases. Not to fieldterms to prevent
// Author=A NEAR/5 Title=B from being legal.
//

proximityclause: a=term WS NEAR? SLASH NUMBER WS b=term -> ^(SLASH $a NUMBER $b);


///////////////////////////////////////////////////////////////////////
//
// Field braces. Sets the default field for the subquery.
//
// To specify stuff like Title={a OR b AND c} which is way nicer than
// Title=a OR Title=b AND Title=c
//

fieldbrace: f=fieldidentifier EQL BRACEOP WS? s=subquery WS? BRACECLS -> ^(BRACEOP $f $s);



///////////////////////////////////////////////////////////////////////
//
// Only simple term-related stuff below this line
//

fieldexpression: fieldterm
                | NOT WS fieldterm -> ^(NOT fieldterm)
                | NOTm fieldterm -> ^(NOT fieldterm); 



fieldterm: phraseterm  // term
          | compop^ phraseterm // >=term
          | fieldidentifier compop^ phraseterm // field>=term
          | fieldbrace; // Field={...}

fragment compop: EQL|GT|LT|GTE|LTE;

fragment fieldidentifier: TEXT;



phraseterm: phrase | wildcardedterm;

phrase: PHRASE;

wildcardedterm: (a=term->$a) ((b+=wildcardterm)+ -> ^(PLACE_CHAR $a $b+))?;

fragment wildcardterm: wildcard | term;
fragment wildcard: PLACE_CHAR | PLACE_CHARS;

term : TEXT | NUMBER;


///////////////////////////////////////////////////////////////////////
//
// Lexer rules -- what do the atoms look like?
//

// If the place holder characters are changed, be sure to update TEXTHEAD.
PLACE_CHAR : '$';
PLACE_CHARS : '#';

PHRASE: '"' .* '"';

PAROP: '(';

PARCLS: ')';

BRACEOP: '{';

BRACECLS: '}';

AND: 'AND';

OR: 'OR';

NEAR: 'NEAR';

SLASH: '/';

NUMBER: '0'..'9';

NOT: 'NOT';
NOTm: '-';

EQL: '='|':';

GT: '>';

LT: '<';

GTE: '>=';

LTE: '<=';


// See http://www.unicode.org/charts/PDF/U0000.pdf for a map between ASCII and unicode.
 
fragment TEXTHEAD: '!'|'%'..'\''|'*'..','|'.'|'0'..'9'|';'|'?'..'z'|'|'|'~'|'\u00a0'..'\udfff'; 
TEXT: TEXTHEAD (TEXTHEAD|NOTm)*;

WS: (' '|'\r'|'\n')+; 
//{$channel = HIDDEN;};
