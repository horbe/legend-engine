parser grammar StateMachineParserGrammar;

import CoreParserGrammar;

options
{
    tokenVocab = StateMachineLexerGrammar;
}

identifier:                      VALID_STRING | STRING | STATE_MACHINE | STATE | TRANSITION | INITIAL | FINAL | FROM | TO | GUARD | TARGET_CLASS
;

// -------------------------------------- DEFINITION --------------------------------------

definition:                                 imports
                                                elementDefinition*
                                            EOF
;
imports:                                    (importStatement)*
;
importStatement:                            IMPORT packagePath PATH_SEPARATOR STAR SEMI_COLON
;
elementDefinition:                          (
                                                stateMachine
                                            )
;

// -------------------------------------- StateMachine --------------------------------------

stateMachine:     STATE_MACHINE qualifiedName
                        BRACE_OPEN
                        (
                            stateMachine_targetClass |
                            stateMachine_state |
                            stateMachine_transition
                        )*
                        BRACE_CLOSE
;

stateMachine_targetClass: TARGET_CLASS COLON qualifiedName SEMI_COLON
;

stateMachine_state: STATE (INITIAL | FINAL)? identifier SEMI_COLON
;

stateMachine_transition: TRANSITION identifier
                            FROM identifier
                            TO identifier
                            (GUARD COLON islandDefinition)?
                            SEMI_COLON
;
