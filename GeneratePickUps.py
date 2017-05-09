import nltk
from nltk import word_tokenize
from nltk import CFG
from nltk.corpus import sentiwordnet as swn
from collections import defaultdict

taggedDict = defaultdict(list)

def createGrammar(filePath):
    
    pickUpLines = open(filePath, 'r')
    if 'S' not in taggedDict:
        taggedDict['S'].append(" -> NP VP");
    for line in pickUpLines:
        token = word_tokenize(line)
        tagged = nltk.pos_tag(token)
        for word, tag in tagged:
            if tag not in taggedDict:
                taggedDict[tag].append(word)
            elif word not in taggedDict.get(tag):
                taggedDict[tag].append(word)


def printToCFG():
     with open("grammar", 'w') as grammar:
        for tag, words in taggedDict.items():
            grammar.write(tag + " ->")
            first_word = True
            for word in words:
                if first_word:
                    grammar.write(" \"" + word + "\"")
                    first_word = False
                else:
                    grammar.write(" | \"" + word + "\"")
            grammar.write('\n')

def main():
    filePath = "pickupLines"
    createGrammar(filePath)
    printToCFG()
    
main()
