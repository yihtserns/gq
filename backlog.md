```
This file must be moved to github issue tracker once we hit 0.1.0
```

# Technical debt
- Rename GqTransformation to GqASTTransformation to follow standard
- Move @Gq out of ast package.
- Rename package to com.ceilfors.groovy instead of transform.
- Remove ast layer in test case
- This code in GqFile seems to be a responsibility of someone else: writer.indentLevel = methodCalls.size()

# Bug 

# Feature
- 0.1.0 - GqSupport.gq to support void return type
- 0.1.0 Support @Gq and GqSupport for groovy scripts e.g. not encapsulated in class
- 0.1.0 Timestamp on each line for readability
- 0.1.0 @Gq Exception - Test - nestedException1 catch exception from nestedexception2 and throw again. Indentation must stay the same.
- @Gq Exception - Print source code context e.g. source code snippets and line numbers
- @Gq(vars=true) to print all variable expression
- Colouring
- GqSupport - Support removing comments from expression text e.g. // in multiline or /* */ in one liner
- Adopt @zefifier groovy-decorator
- q.d