#include <bits/stdc++.h>

using namespace std;
using namespace chrono;
typedef long long ll;
#define MAX 10000000
#define INF MAX+1

ll targetNr = -1;
int figure = -1;
vector<ll> figurePows;
// predNr, predOp, predOpNr
map<ll, tuple<ll, int, ll>> pred;

map<ll, int> curBest;
vector<vector<ll>> prevNrs;

int curAmount;
int nrFound;

// --- CUSTOM OPERATIONS ---
ll multiply(ll nr1, ll nr2) {
    if (to_string(nr1).length() + to_string(nr2).length() > to_string(MAX).length()) {
        return -1;
    }
    ll res = nr1 * nr2;
    if (res < -MAX || MAX < res) {
        return -1;
    }
    return res;
}

ll fastPow(ll base, ll exp) {
    ll res = 1;
    ll temp;
    while (exp > 0) {
        if (exp % 2 == 1) {
            temp = multiply(res, base);
            if (temp == -1) {
                return -1;
            }
            res = temp;
        }
        temp = multiply(base, base);
        if (temp == -1) {
            return -1;
        }
        base = temp;
        exp /= 2;
    }
    return res;
}

vector<ll> factorials = {1, 1};
ll maxFact = INT_MAX;

ll fastFact(ll nr) {
    if (nr > maxFact) {
        return -1;
    }
    while (factorials.size() <= nr) {
        ll curFact = multiply(factorials[factorials.size() - 1], factorials.size());
        if (curFact == -1) {
            maxFact = factorials.size() - 1;
            return -1;
        }
        factorials.push_back(curFact);
    }
    return factorials[nr];
}


// --- GENERATE NRS USABLE IN TERM ---
bool isFigurePow(ll nr) {
    string s = to_string(nr);
    for (int i = 0; i < s.length(); i++) {
        if (s[i] != ((char) (figure + 48))) {
            return false;
        }
    }
    return true;
}

ll getFigurePow(int length) {
    while (figurePows.size() < length) {
        figurePows.push_back(figurePows[figurePows.size() - 1] * 10 + figure);
    }
    return figurePows[length - 1];
}

// --- TIMINGS ---
steady_clock::time_point tBegin;
steady_clock::time_point tEnd;

void timeBegin() {
    tBegin = steady_clock::now();
}

void timeEnd() {
    tEnd = steady_clock::now();
    cout << " >> " << duration_cast<milliseconds>(tEnd - tBegin).count() << "ms" << endl;
}

// --- ALGORITHM ---
bool checkOp(ll opRes, tuple<ll, int, ll> op, int figureAmount) {
    if (0 < opRes && opRes <= MAX && (curBest[opRes] == 0 || curBest[opRes] > figureAmount)) {
        pred[opRes] = op;
        curBest[opRes] = figureAmount;
        prevNrs[figureAmount].push_back(opRes);
        if (opRes == targetNr) {
            nrFound = figureAmount;
        }
        return true;
    }
    return false;
}

bool check(ll prevNr, ll curNr, int figureAmount) {
    if (nrFound != 0 && figureAmount >= nrFound) {
        return false;
    }
    bool found = false;
    // additional operation: factorial
    if (prevNr == INF && curNr != 0) {
        ll fact = fastFact(curNr);
        if (fact != -1) {
            found = max(found, checkOp(fact, {curNr, 6, prevNr}, figureAmount));
        }
        return found;
    }

    ll plus = prevNr + curNr;

    found = max(found, checkOp(plus, {prevNr, 1, curNr}, figureAmount));

    ll minus;
    // only use positive results - negative terms can be replaced by positive ones and inverted operation sign
    if (prevNr > curNr) {
        minus = prevNr - curNr;
        found = max(found, checkOp(minus, {prevNr, 2, curNr}, figureAmount));
    } else {
        minus = curNr - prevNr;
        found = max(found, checkOp(minus, {curNr, 2, prevNr}, figureAmount));
    }

    // check beforehand, whether result would be out of bounds
    ll multi = multiply(prevNr, curNr);
    if (multi != -1) {
        found = max(found, checkOp(multi, {prevNr, 3, curNr}, figureAmount));
    }

    ll div;
    if (curNr != 0 && prevNr % curNr == 0) {
        div = prevNr / curNr;
        found = max(found, checkOp(div, {prevNr, 4, curNr}, figureAmount));
    }
    if (prevNr != 0 && curNr % prevNr == 0) {
        div = curNr / prevNr;
        found = max(found, checkOp(div, {curNr, 4, prevNr}, figureAmount));
    }

    // additional operation: pow
    ll power = fastPow(prevNr, curNr);
    if (curNr != 0 && power != -1) {
        found = max(found, checkOp(power, {prevNr, 5, curNr}, figureAmount));
    }
    power = fastPow(curNr, prevNr);
    if (prevNr != 0 && power != -1) {
        found = max(found, checkOp(power, {curNr, 5, prevNr}, figureAmount));
    }

    return found;
}

string reconstruct(ll nr) {
    // only used for factorials (no second number for operation)
    if (nr == INF) {
        return "";
    }
    if (isFigurePow(nr)) {
        return to_string(nr);
    }
    vector<pair<ll, char>> ops;
    while (nr != 0) {
        ll predNr, opNr;
        int op;
        tie(predNr, op, opNr) = pred[nr];
        char opChar = op == 1 ? '+' : op == 2 ? '-' : op == 3 ? '*' : op == 4 ? '/' : op == 5 ? '^' : '!';
        ops.push_back({opNr, opChar});
        nr = predNr;
    }
    string res = reconstruct(ops[ops.size() - 1].first);
    for (int i = ops.size() - 2; i >= 0; i--) {
        res = "(" + res + ops[i].second + reconstruct(ops[i].first) + ")";
    }
    return res;
}

void run() {
    timeBegin();
    // clear prev variables - only needed when run multiple times
    pred.clear();
    curBest.clear();
    // variable initialization
    figurePows = {figure};
    curAmount = 0;
    nrFound = 0;
    prevNrs = {{0}};
    curBest[0] = 0;

    // algorithm
    while (!nrFound || nrFound > curAmount + 1) {
        curAmount++;
        bool found = false;
        prevNrs.push_back(vector<ll>());

        // try calc with previous nrs
        for (int i = 1; i <= curAmount / 2; i++) {
            int secondAmount = curAmount - i;
            for (ll prevNr : prevNrs[i]) {
                for (ll curNr : prevNrs[secondAmount]) {
                    found = max(found, check(prevNr, curNr, curAmount));
                }
            }
        }
        // try calc with prev nrs & possible "potency" 2->11, 3->111...
        for (int i = 0; i < curAmount; i++) {
            int dif = curAmount - i;
            ll curFigPow = getFigurePow(dif);
            for (ll prevNr : prevNrs[i]) {
                found = max(found, check(prevNr, curFigPow, curAmount));
            }
        }

        // try factorials
        queue<ll> factQueue;
        for (ll curNr : prevNrs[curAmount]) {
            factQueue.push(curNr);
        }
        while (!factQueue.empty()) {
            ll curNr = factQueue.front();
            factQueue.pop();
            if (check(INF, curNr, curAmount)) {
                factQueue.push(fastFact(curNr));
                found = true;
            }
        }

        if (!found) {
            if (!nrFound) {
                cout << "Boundaries too small - no term found" << endl;
                return;
            }
            break;
        }
    }
    timeEnd();

    cout << reconstruct(targetNr) << " ~ " << nrFound << endl;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(0);

    while (targetNr == -1) {
        cout << "Bitte geben sie die darzustellende Nummer ein:" << endl;
        ll temp;
        cin >> temp;
        if (temp >= 0) {
            targetNr = temp;
        }
    }
    steady_clock::time_point oaBegin = steady_clock::now();
    for(int i = 1; i <= 9; i++) {
        figure = i;
        cout << "[" << i << "]";
        run();
    }
    steady_clock::time_point oaEnd = steady_clock::now();
    cout << "Gesamt >> " << duration_cast<milliseconds>(oaEnd - oaBegin).count() << "ms" << endl;
}