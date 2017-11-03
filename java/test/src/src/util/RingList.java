/**
 * @file RingList.java
 */

package util;

public class RingList
{
    /**
     * +------+------+--h---+------+------+------+--t---+------+------+------+
     * |null  |null  |(EL^) |(EL^) |(EL^) |(EL^) |null  |null  |null  |null  |
     * +------+------+--^---+------+------+------+--^---+------+------+------+
     *                  |                           |
     *                  |                           |
     *                  h                           t
     */
    private int _h, _t, _size;
    private Object[] _buf;    

    public RingList(int size)
    {
        if (1 >= size) {
            size = 5;
        }

        _buf = new Object[size];
        _h = 0;
        _t = 0;
        _size = 0;
    }

    public synchronized Object getEl(Object el)
    {
        if (null != _buf[_h]) {
            Object temp = _buf[_h];

            if (null == el || temp.equals(el)) {
                _buf[_h] = null;
                _h = (_h + 1) % _buf.length;
            }
            else {
                int i;

                temp = null;
                i = _h;
                do {
                    i = (i + 1) % _buf.length;
                    if (el.equals(_buf[i])) {
                        temp = _buf[i]; /*reserve result*/
                        shiftEls(i, _h, _t);

                        break;
                    }
                } while (i != _t);
            }
            
            --_size;
            
            return temp;
        }
        else {
            return null;
        }
    }

    public synchronized void addEl(Object el)
    {
        int next = (_t + 1) % _buf.length;

        if (null == el) {
            return;
        }

        if (_h == _t && null != _buf[_t]) {
            Object temp[] = new Object[_buf.length << 1];
            temp[_buf.length] = el;
            System.arraycopy(_buf, _h, temp, 0, _buf.length - _h);
            System.arraycopy(_buf, 0, temp, _buf.length - _h, _h);

            _h = 0;
            next = _buf.length + 1;

            _buf = null;
            _buf = temp;
        }
        else {
            _buf[_t] = el;
        }

        _t = next;
        ++_size;
    }
    
    public synchronized int getSize()
    {
        return _size;
    }

    private void shiftEls(int nullPos, int head, int tail)
    {
        int shiftCnt1, shiftCnt2;

        shiftCnt1 = nullPos - head;
        if (0 > shiftCnt1) {
            shiftCnt1 += _buf.length;
        }

        shiftCnt2 = tail - nullPos;
        if (0 > shiftCnt2) {
            shiftCnt2 += _buf.length;
        }

        if (shiftCnt1 <= shiftCnt2) {
            int pre;

            pre = nullPos - 1;
            if (0 > pre) {
                pre += _buf.length;
            }

            while (pre != _h) {
                _buf[nullPos] = _buf[pre];

                --nullPos;
                if (nullPos < 0) {
                    nullPos += _buf.length;
                }

                pre = nullPos - 1;
                if (0 > pre) {
                    pre += _buf.length;
                }
            }
            _buf[nullPos] = _buf[pre];

            _buf[_h] = null;
            _h = (head + 1) % _buf.length;
        }
        else {
            int next;

            next = (nullPos + 1) % _buf.length;

            while (next != _t) {
                _buf[nullPos] = _buf[next];
                nullPos = (nullPos + 1) % _buf.length;
                next = (nullPos + 1) % _buf.length;
            }

            --_t;
            if (0 > _t) {
                _t += _buf.length;
            }
            _buf[_t] = null;
        }
    }
}